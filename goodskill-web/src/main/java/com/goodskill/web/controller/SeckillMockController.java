package com.goodskill.web.controller;

import com.alibaba.fastjson2.JSON;
import com.goodskill.core.enums.SeckillSolutionEnum;
import com.goodskill.core.exception.CommonException;
import com.goodskill.core.feign.SeckillFeignClient;
import com.goodskill.core.info.Result;
import com.goodskill.core.pojo.dto.SeckillMockRequestDTO;
import com.goodskill.core.pojo.dto.SeckillWebMockRequestDTO;
import com.goodskill.web.util.TaskTimeCaculateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.goodskill.core.enums.SeckillSolutionEnum.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 模拟秒杀场景，可在swagger界面中触发操作
 *
 * @author heng
 * @date 2018/09/02
 */
@Tag(name = "模拟秒杀场景(无需登录)")
@RestController
@Slf4j
@Validated
public class SeckillMockController {

    @Resource
    private SeckillFeignClient seckillFeignClient;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 用于生成秒杀用户id
     */
    private final AtomicInteger SECKILL_PHONE_NUM_COUNTER = new AtomicInteger(0);

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于单机模式，扩展性差。
     */
    @Operation(summary = "秒杀场景一(sychronized同步锁实现)")
    @PostMapping("/sychronized")
    public Result<Long> doWithSychronized(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        Long l = processSeckill(dto, SYCHRONIZED);
        return Result.ok(l);
        //待mq监听器处理完成打印日志，不在此处打印日志
    }

    /**
     * 通过同步锁控制秒杀并发（秒杀未完成阻塞主线程）
     * 场景二：初始化当前库存为1000，通过线程池调度，模拟总共有2000人参与秒杀，期望值为最后成功笔数为1000
     * 结果：多次运行，最终的结果为1000
     * 总结：加上同步锁可以解决秒杀问题，适用于分布式环境，但速度不如加同步锁。
     */
    @Operation(summary = "秒杀场景二(redis分布式锁实现)", description = "秒杀场景二(redis分布式锁实现)", method = "POST")
    @PostMapping("/redisson")
    public Result doWithRedissonLock(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        Long l = processSeckill(dto, REDISSON_LOCK);
        return Result.ok(l);
    }


    /**
     *
     */
    @Operation(summary = "秒杀场景七(zookeeper分布式锁)")
    @RequestMapping(value = "/zookeeperLock", method = POST, produces = {
            "application/json;charset=UTF-8"})
    public Result doWithZookeeperLock(@RequestBody @Valid SeckillWebMockRequestDTO dto) {
        Long l = processSeckill(dto, ZOOKEEPER_LOCK);
        return Result.ok(l);
    }

    /**
     * 获取秒杀活动最新任务id fixme 目前只能拿到全局最新的任务id，不能区分秒杀活动id
     * @param seckillId 秒杀活动id
     * @return 秒杀活动最新任务id
     */
    @PostMapping("/task-info")
    public Result<Long> getTaskId(@RequestParam Long seckillId) {
        return Result.ok(Long.valueOf(stringRedisTemplate.opsForValue().get("SECKILL_TASK_ID_COUNTER")));
    }

    /**
     * 获取任务耗时统计信息
     *
     * @param seckillId 秒杀活动的ID
     * @return 返回一个Result对象，其中包含格式化后的任务时间信息字符串
     */
    @GetMapping("/task-time-info")
    public Result<String> getTaskTimeInfo(@RequestParam Long seckillId) {
        return Result.ok(TaskTimeCaculateUtil.prettyPrint(String.valueOf(stringRedisTemplate.opsForValue().get("SECKILL_TASK_ID_COUNTER"))));
    }

    /**
     * 获取最新任务的详细信息
     *
     * @return 返回一个Result对象，其中包含最新任务的详细信息
     */
    @GetMapping("/task-info/latest")
    public Result<Map<String, Object>> getLatestTaskDetails() {
        String latestTaskId = stringRedisTemplate.opsForValue().get("SECKILL_TASK_ID_COUNTER");
        if (latestTaskId == null) {
            return Result.fail("暂无任务数据");
        }
        Map<String, Object> taskDetails = TaskTimeCaculateUtil.getTaskDetails(latestTaskId);
        if (taskDetails == null) {
            return Result.fail("任务不存在或已过期");
        }
        return Result.ok(taskDetails);
    }

    /**
     * 获取指定任务的详细信息
     *
     * @param taskId 任务ID
     * @return 返回一个Result对象，其中包含任务的详细信息
     */
    @GetMapping("/task-info/{taskId}")
    public Result<Map<String, Object>> getTaskDetails(@PathVariable String taskId) {
        Map<String, Object> taskDetails = TaskTimeCaculateUtil.getTaskDetails(taskId);
        if (taskDetails == null) {
            return Result.fail("任务不存在或已过期");
        }
        return Result.ok(taskDetails);
    }

    /**
     * 获取全部任务的详细信息
     *
     * @return 返回一个Result对象，其中包含所有任务的详细信息
     */
    @GetMapping("/task-info/all")
    public Result<Map<String, Map<String, Object>>> getAllTaskDetails() {
        Map<String, Map<String, Object>> allTaskDetails = TaskTimeCaculateUtil.getAllTaskDetails();
        return Result.ok(allTaskDetails);
    }


    /**
     * 准备商品库存
     *
     * @param seckillId
     * @param seckillCount
     * @param name
     * @param taskId
     */
    private void prepareSeckill(long seckillId, int seckillCount, String name, String taskId) {
        seckillFeignClient.prepareSeckill(seckillId, seckillCount, taskId);
        TaskTimeCaculateUtil.startTask("活动id:" + seckillId + "," + name, taskId);
    }

    /**
     * 变更线程池参数
     *
     * @param dto 参数
     */
    private void changeThreadPoolParam(SeckillWebMockRequestDTO dto) {
        try {
            if (dto.getCorePoolSize() != null && dto.getCorePoolSize() > 0) {
                int corePoolSize = taskExecutor.getCorePoolSize();
                taskExecutor.setCorePoolSize(dto.getCorePoolSize());
                log.info("#changeThreadPoolParam 更新核心线程数参数生效, 原参数值:{},当前值:{}", corePoolSize, dto.getCorePoolSize());
            }
            if (dto.getMaxPoolSize() != null && dto.getMaxPoolSize() > 0) {
                int maxPoolSize = taskExecutor.getMaxPoolSize();
                taskExecutor.setMaxPoolSize(dto.getMaxPoolSize());
                log.info("#changeThreadPoolParam 更新最大线程数参数生效, 原参数值:{},当前值:{}", maxPoolSize, dto.getMaxPoolSize());
            }
        } catch (IllegalArgumentException e) {
            log.warn("#changeThreadPoolParam 核心线程数不能大于最大线程数，当前最大线程数:{}，当前核心线程数:{}", taskExecutor.getMaxPoolSize(), taskExecutor.getCorePoolSize(), e);
            throw new CommonException("线程池参数不合法，请重新设置！");
        }
    }

    /**
     * 执行秒杀程序
     *
     * @param dto                 秒杀请求
     * @param seckillSolutionEnum 秒杀策略
     */
    private Long processSeckill(SeckillWebMockRequestDTO dto, SeckillSolutionEnum seckillSolutionEnum) {
        return processSeckill(dto, seckillSolutionEnum, null);
    }

    /**
     * 执行秒杀程序
     *
     * @param dto                 秒杀请求
     * @param seckillSolutionEnum 秒杀策略
     * @param runnable            待执行的任务
     * @return 秒杀任务id
     */
    private Long processSeckill(SeckillWebMockRequestDTO dto, SeckillSolutionEnum seckillSolutionEnum, Runnable runnable) {
        long seckillId = dto.getSeckillId();
        int seckillCount = dto.getSeckillCount();
        int requestCount = dto.getRequestCount();
        Long seckillTaskIdCounter = stringRedisTemplate.opsForValue().increment("SECKILL_TASK_ID_COUNTER");
        String taskId = String.valueOf(seckillTaskIdCounter);

        // 立即返回任务ID，异步执行秒杀逻辑
        taskExecutor.execute(() -> {
            try {
                log.debug("#processSeckill start count:{},当前线程池队列长度:{},线程数:{},是否空:{}", SECKILL_PHONE_NUM_COUNTER.get(),
                        taskExecutor.getThreadPoolExecutor().getQueue().size(),
                        taskExecutor.getPoolSize(), taskExecutor.getThreadPoolExecutor().getQueue().isEmpty());
                // 初始化库存数量
                prepareSeckill(seckillId, seckillCount, seckillSolutionEnum.getName(), taskId);
                changeThreadPoolParam(dto);
                log.info("{}开始时间:{}, 秒杀id:{}, 任务Id:{}", seckillSolutionEnum.getName(), new Date(), seckillId, taskId);
                Runnable finalRunnable;
                if (runnable == null) {
                    // 默认的执行方法
                    finalRunnable = () -> {
                        String phoneNumber = String.valueOf(SECKILL_PHONE_NUM_COUNTER.incrementAndGet());
                        seckillFeignClient.execute(new SeckillMockRequestDTO(seckillId, 1, phoneNumber, taskId),
                                seckillSolutionEnum.getCode());
                    };
                } else {
                    finalRunnable = runnable;
                }
                for (int i = 0; i < requestCount; i++) {
                    if (log.isDebugEnabled()) {
                        log.debug("#processSeckill begin count:{},当前线程池队列长度:{},线程数:{},是否空:{}", SECKILL_PHONE_NUM_COUNTER.get(),
                                taskExecutor.getThreadPoolExecutor().getQueue().size(),
                                taskExecutor.getPoolSize(), taskExecutor.getThreadPoolExecutor().getQueue().isEmpty());
                    }
                    taskExecutor.execute(finalRunnable);
                }
            } catch (Exception e) {
                log.error("执行秒杀任务失败，任务ID:{}", taskId, e);
            }
        });

        return seckillTaskIdCounter;
    }

}
