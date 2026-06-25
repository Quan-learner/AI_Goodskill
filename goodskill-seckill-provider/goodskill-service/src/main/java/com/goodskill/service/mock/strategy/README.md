
# 多策略并发秒杀引擎 — 面试介绍指南

> 基于项目 goodsKill（goodskill）的核心设计整理

---

## 一、整体概述

**多策略并发秒杀引擎**是一个基于 **策略模式（Strategy Pattern）** 设计的秒杀系统核心模块。它通过抽象统一的秒杀接口，将三种不同的锁机制（Synchronized、Redisson、ZooKeeper）封装为可互换的策略实现，并在运行时根据传入的策略编号动态路由到对应的秒杀方案。该设计既杜绝了高并发场景下的超卖与少卖问题，又保持了系统的可扩展性——新增一种锁方案只需新增一个实现类。

---

## 二、核心架构（三层结构）

### 第1层：策略抽象接口

```
GoodsKillStrategy 接口
```

- 定义统一的 `execute(SeckillMockRequestDTO requestDto)` 方法
- 所有秒杀策略实现此接口，确保调用方与具体实现解耦
- 位于 `com.goodskill.service.mock.strategy` 包

```java
public interface GoodsKillStrategy {
    void execute(SeckillMockRequestDTO requestDto);
}
```

### 第2层：三种具体策略实现

| 策略 | 锁类型 | 适用场景 | 核心原理 |
|------|--------|---------|---------|
| **SynchronizedLockStrategy** | JVM 级同步锁（synchronized） | 单机部署、单体应用 | 通过 ConcurrentHashMap 对每个秒杀商品 ID 维护一个锁对象，锁细化到商品粒度 |
| **RedissonStrategy** | Redis 分布式锁（Redisson） | 分布式部署 | 基于 RedissonClient 的 RLock，自动续期、可重入 |
| **ZookeeperLockStrategy** | ZooKeeper 分布式锁（Curator） | 分布式部署、高一致性要求场景 | 基于 Apache Curator 的 InterProcessMutex 互斥锁 |

### 第3层：运行时策略路由

```
SeckillServiceImpl.execute(SeckillMockRequestDTO, int strategyNumber)
```

- 通过 Spring 自动注入 `List<GoodsKillStrategy>` 获取所有策略 Bean
- 根据 `strategyNumber` 匹配 `GoodsKillStrategyEnum` 中的 className 前缀
- 使用 `filter + findFirst` 定位唯一的策略并执行

```java
public void execute(SeckillMockRequestDTO requestDto, int strategyNumber) {
    goodskillStrategies.stream()
        .filter(n -> n.getClass().getName().startsWith(
            Objects.requireNonNull(GoodsKillStrategyEnum.stateOf(strategyNumber)).getClassName()))
        .findFirst().ifPresent(n -> n.execute(requestDto));
}
```

策略枚举映射：

```java
SYNCHRONIZED(1, "Synchronized同步锁", SynchronizedLockStrategy)
REDISSON(2, "redis分布式锁", RedissonStrategy)
ZOOKEEPER_LOCK(7, "zookeeper分布式锁", ZookeeperLockStrategy)
```

---

## 三、三种锁方案的详细设计

### 1. SynchronizedLockStrategy — JVM 级同步锁

**设计思路**
- 使用 `ConcurrentHashMap<Long, Object>` 为每个秒杀商品 ID 绑定一个独立的锁对象
- 只在 `synchronized (seckillIdList.get(seckillId))` 块内执行数据库操作
- 锁粒度细化到"单个商品"，而非锁住整个方法或整个类

**工作流程**
```
① 从 ConcurrentHashMap 获取该 seckillId 对应的锁对象
② synchronized 加锁
③ 查询当前库存 (seckillMapper.selectById)
④ 若库存 > 0，执行减库存 + 插入秒杀成功记录（在同一事务中）
⑤ 若库存 = 0，触发状态机发送售罄通知
⑥ 锁释放
```

**如何防止超卖**
- synchronized 保证同一时刻只有一个线程能操作某个商品的库存
- 查库存与减库存是原子操作（在锁内连续执行）

**如何防止少卖**
- 采用 `ConcurrentHashMap.putIfAbsent` 式的惰性初始化（源码中先 get 再 put），不过这里在极高并发下可能有轻微竞态，但在 synchronized 保护的业务逻辑层面已经是正确的
- 实际上更完善的写法可以用 `computeIfAbsent`，但整体功能正确

**局限性**
- 仅限于单机部署，多实例部署时 JVM 锁不跨进程

---

### 2. RedissonStrategy — Redis 分布式锁

**设计思路**
- 使用 Redisson 的 `RLock`（实现了 JUC `Lock` 接口）
- 以秒杀活动 ID 作为锁的 key
- Redisson 内置**看门狗机制**（Watchdog），自动续期，防止锁超时误释放

**工作流程**
```
① redissonClient.getLock(seckillId) 获取锁对象
② lock.lock() 加锁（默认 30s 自动续期）
③ 委托 SeckillExecutor.dealSeckill() 执行核心秒杀逻辑
④ finally 块中 lock.unlock() 释放锁
```

**核心秒杀逻辑（SeckillProcedureExecutor.dealSeckill）**
```
① 构造 SuccessKilledDTO（含本机 IP 信息）
② 调用 seckillService.reduceNumber(successKilled)
   → 插入秒杀成功记录 + 带乐观锁条件的库存扣减
   → UPDATE seckill SET number = number - 1 
     WHERE number > 0 AND end_time > now AND start_time < now
   → 利用 MySQL 行级锁 + 乐观锁条件双重保障
③ 若返回 < 1（库存不足），触发状态机发送售罄通知
④ 利用 Redis 的 setIfAbsent 保证售罄通知只发送一次
```

**如何防止超卖**
- Redisson 分布式锁保证跨 JVM 的互斥访问
- 库存扣减时附带 `number > 0` 的乐观锁条件，数据库层面再兜底

**如何防止少卖**
- Redisson 可重入锁+看门狗自动续期，不会因为锁超时释放而导致并发写入
- 分布式锁确保多个实例间同一 moment 只有一个线程在操作库存

**优势**
- 比 ZooKeeper 方案吞吐量更高（Redis 基于内存，性能好）
- 看门狗自动续期避免了锁超时的尴尬
- 可重入，避免死锁

---

### 3. ZookeeperLockStrategy — ZooKeeper 分布式锁

**设计思路**
- 使用 Apache Curator 框架提供的 `InterProcessMutex` 可重入互斥锁
- 锁路径格式：`/goodskill/{seckillId}`
- 支持超时等待（1000ms 获取锁等待时间）

**工作流程**
```
① zookeeperLockUtil.lock(seckillId) 获取 InterProcessMutex
   → Curator 在 ZooKeeper 中创建临时顺序节点
   → acquire(1000ms) 等待获取锁
② 获取成功则委托 SeckillExecutor.dealSeckill() 执行核心逻辑
③ finally 块中 lock.release() 释放锁
```

**ZookeeperLockUtil 实现**
- 使用 `ExponentialBackoffRetry` 指数退避重试策略
- `@PostConstruct` 中初始化 CuratorFramework 客户端
- `@PreDestroy` 时优雅关闭客户端

**优势**
- 强一致性（ZooKeeper 的 ZAB 协议保证）
- 客户端断开连接时锁自动释放（临时节点特性），不会死锁

**劣势**
- 性能相对 Redis 较低（ZooKeeper 写操作需过半确认）
- 羊群效应（大量客户端同时监听同一节点）

---

## 四、防止超卖与少卖的完整保障体系

该引擎从四个层面层层设防，确保库存准确：

| 层级 | 机制 | 说明 |
|------|------|------|
| **应用层** | 分布式锁 / 同步锁 | 保证同一时刻只有一个线程操作某商品库存 |
| **数据库层** | 乐观锁（CAS） | UPDATE 时附加 `number > 0` 条件，影响行数为 0 则判定失败 |
| **状态机** | Spring StateMachine | 管理秒杀活动生命周期：IN_PROGRESS → END，防止结束后继续秒杀 |
| **通知层** | Redis setIfAbsent | 售罄通知保证只发送一次，避免重复通知导致少卖 |


## 五、设计模式运用总结

| 设计模式 | 运用位置 | 作用 |
|---------|---------|------|
| **策略模式** | GoodsKillStrategy + 三个实现 | 将不同的锁方案封装为可互换的策略 |
| **工厂方法（变体）** | GoodsKillStrategyEnum + Spring DI | 通过枚举 + 自动注入实现策略的创建和查找 |
| **模板方法** | SeckillExecutor 接口 + SeckillProcedureExecutor | 定义秒杀处理的标准化流程 |
| **责任链模式** | PreRequestPipeline + PreRequestHandler | 秒杀前的预处理流水线（DB/MongoDB/Redis/状态机） |
| **状态模式** | StateMachineConfig（Spring StateMachine） | 管理秒杀活动的状态流转 |

---

## 六、面试话术建议

### 当面试官问"介绍一下这个项目"

> "这个项目的核心模块是一个多策略并发秒杀引擎。它采用策略模式，抽象了一个 GoodsKillStrategy 接口，然后实现了三种不同的锁方案：Synchronized、Redisson 和 ZooKeeper。调用方只需传入一个策略编号，系统就会动态路由到对应的实现，完全不需要修改调用代码。

> 以 Redisson 方案为例，它的处理流程是：先获取 Redisson 分布式锁，确保同一时刻只有一个线程在处理某个商品的秒杀请求，然后执行库存扣减。库存扣减并不是简单的 UPDATE，而是用了数据库层面的乐观锁——在 UPDATE 语句中带上 number > 0 的条件，如果影响行数为 0 就说明库存已经没了。这样就形成了双层防护：应用层的分布式锁保证线程互斥，数据库层的乐观锁做最后的兜底。

> 三种方案各有优劣：Synchronized 最简单，但只适用于单机；Redisson 性能好，适合大部分分布式场景；ZooKeeper 一致性最强，适合对数据一致性要求极高的场景。这个架构最大的好处是——如果以后要加一种新的锁方案，比如基于数据库的乐观锁或基于 etcd 的分布式锁，只需要新建一个实现类就行，完全不需要改动现有代码。"

### 当面试官问"怎么防止超卖"（重点问题）

> "我们从四个层面来保障。第一层，应用层的互斥锁——Synchronized 或分布式锁，确保同一时间只有一个线程在操作库存。第二层，数据库层面的乐观锁——UPDATE 语句中带上 number > 0 的条件，这个 CAS 操作是原子性的，数据库本身保证不会超卖。第三层，状态机管理——秒杀活动结束后会切换到 END 状态，后续所有请求都会被拦截。第四层，售罄通知的去重——用 Redis 的 setIfAbsent 保证售罄通知只发送一次，避免重复通知导致少卖。简单来说就是：锁是门卫，乐观锁是安检，状态机是总闸，Redis 是最后一道保险。"

### 当面试官问"为什么用策略模式"

> "因为我们预见到秒杀的锁方案可能会有多种选择，而且这个选择可能会变化。策略模式可以把'怎么加锁'这个行为抽象出来，让三种方案实现同一个接口，在使用时通过路由选择具体的策略。这样做的好处有三个：第一，新增方案不需要改现有代码，符合开闭原则；第二，方便做对比测试——这个项目实际上就是用三种方案做压测对比的；第三，代码更清晰，每种锁方案的逻辑都封装在自己的类里，不像 if-else 那样分散在各处。"

---

祝你面试顺利！
