package com.snwolf.chat.common.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Aspect
@Slf4j
public class GetRunningTimeAspect {

    @Pointcut("@annotation(com.snwolf.chat.common.common.annotation.GetRunningTime)")
    private void pointCut(){}

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Object result = joinPoint.proceed();
        long end = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        log.info("{} 方法执行时间: {}", joinPoint.getSignature().getName(), end - begin);
        return result;
    }
}