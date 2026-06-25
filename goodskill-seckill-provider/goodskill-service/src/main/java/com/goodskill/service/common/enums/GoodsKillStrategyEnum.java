package com.goodskill.service.common.enums;

import com.goodskill.core.enums.SeckillSolutionEnum;
import com.goodskill.service.mock.strategy.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秒杀策略枚举，通过枚举避免暴露具体方法
 *
 * @author heng
 */
@Getter
@AllArgsConstructor
public enum GoodsKillStrategyEnum {

    /**
     *
     */
    SYNCHRONIZED(SeckillSolutionEnum.SYCHRONIZED, "Synchronized同步锁", SynchronizedLockStrategy.class.getName()),
    REDISSON(SeckillSolutionEnum.REDISSON_LOCK, "redis分布式锁", RedissonStrategy.class.getName()),
    ZOOKEEPER_LOCK(SeckillSolutionEnum.ZOOKEEPER_LOCK, "zookeeper分布式锁", ZookeeperLockStrategy.class.getName()),
    ;

    private final SeckillSolutionEnum seckillSolutionEnum;

    private final String strategyName;

    private final String className;

    public static GoodsKillStrategyEnum stateOf(int code) {
        for (GoodsKillStrategyEnum state : values()) {
            if (state.seckillSolutionEnum.getCode() == code) {
                return state;
            }
        }
        return null;
    }
}
