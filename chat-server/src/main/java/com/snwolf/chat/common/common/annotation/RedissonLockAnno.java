package com.snwolf.chat.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLockAnno {

    /**
     * key 的前缀, 默认取方法的全限定名, 默认值放在aop中处理
     * @return
     */
    String keyPrefix() default "";

    /**
     * 真实的lockKey, 支持SpringEL表达式
     * @return
     */
    String lockKey();

    /**
     * 等待时间, 默认为-1, 快速失败
     * @return
     */
    int waitTime() default -1;

    /**
     * 等待的时间单位, 默认毫秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}