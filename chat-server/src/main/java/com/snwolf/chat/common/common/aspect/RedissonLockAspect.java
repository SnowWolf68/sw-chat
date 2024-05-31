package com.snwolf.chat.common.common.aspect;

import com.snwolf.chat.common.common.annotation.RedissonLockAnno;
import com.snwolf.chat.common.common.service.LockService;
import com.snwolf.chat.common.common.utils.SpELUtils;
import jodd.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Component
@Aspect
@Order(0)   // 确保分布式锁比事务先实现, 锁在事务外
public class RedissonLockAspect {

    @Resource
    private LockService lockService;

    @Around("@annotation(redissonLockAnno)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLockAnno redissonLockAnno) throws Throwable {
        // 获取prefix
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String prefix = StringUtil.isBlank(redissonLockAnno.keyPrefix()) ?
                SpELUtils.getMethodKey(method) : redissonLockAnno.keyPrefix();
        String key = SpELUtils.parseSpEL(method, joinPoint.getArgs(), redissonLockAnno.lockKey());
        return lockService.excueteWithLock(prefix + ":" + key, redissonLockAnno.waitTime(), redissonLockAnno.timeUnit(), joinPoint::proceed);
    }
}