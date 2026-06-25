package com.goodskill.core.enums;

import lombok.Getter;

/**
 * 秒杀场景枚举
 *
 * @author techa03
 * @date 2019/4/4
 */
@Getter
public enum SeckillSolutionEnum {
    /**
     *
     */
    SYCHRONIZED(1,"sychronized同步锁实现"),
    REDISSON_LOCK(2,"redis分布式锁实现"),
    ZOOKEEPER_LOCK(7, "zookeeper分布式锁"),
    ;

    private final int code;
    private final String name;

    SeckillSolutionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
