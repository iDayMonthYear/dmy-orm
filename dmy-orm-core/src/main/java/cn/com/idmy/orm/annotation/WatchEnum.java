package cn.com.idmy.orm.annotation;

import cn.com.idmy.base.model.IEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WatchEnum {
    /**
     * 要监听的实体类
     */
    Class<?> entity();

    /**
     * 要监听的枚举类型
     */
    Class<? extends IEnum<?>> value();

    /**
     * 数据库操作类型
     */
    WatchAction action() default WatchAction.INSERT;

    /**
     * 执行时机
     */
    WatchTiming timing() default WatchTiming.BEFORE;

    enum WatchAction {
        INSERT,
        UPDATE,
        DELETE
    }

    enum WatchTiming {
        BEFORE,
        AFTER
    }
} 