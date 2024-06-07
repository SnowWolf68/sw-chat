package com.snwolf.swchat.transaction.aspect;

import cn.hutool.json.JSONUtil;
import com.snwolf.swchat.transaction.annotation.SecureInvoke;
import com.snwolf.swchat.transaction.domain.SecureInvokeStatusEnum;
import com.snwolf.swchat.transaction.domain.dto.SecureInvokeDTO;
import com.snwolf.swchat.transaction.domain.entity.SecureInvokeRecord;
import com.snwolf.swchat.transaction.service.SecureInvokeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Aspect
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)  // 确保最先执行, +1 是为了保证不影响 最高优先级 组件的正常执行
public class SecureInvokeAspect {

    @Resource
    private SecureInvokeRecordService secureInvokeRecordService;

    @Around("@annotation(secureInvoke)")
    public Object around(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) throws Throwable {
        boolean async = secureInvoke.async();
        // 判断当前线程中是否存在事务
        boolean isInTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        if(!isInTransaction){
            // 当前不存在事务, 直接执行目标方法
            return joinPoint.proceed();
        }
        // 当前存在事务
        // 首先获取当前目标方法的 全限定类名, 方法名, 参数类型, 传入的参数
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 参数类型使用json格式封装
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        String className = method.getDeclaringClass().getName();
        // 传入的具体参数也使用json类型封装
        Object[] args = joinPoint.getArgs();
        // 构造SecureInvokeDTO
        SecureInvokeDTO secureInvokeDTO = SecureInvokeDTO.builder()
                .className(className)
                .methodName(method.getName())
                .parameterTypes(JSONUtil.toJsonStr(parameters))
                .args(JSONUtil.toJsonStr(args))
                .build();
        // 构造向本地消息表中插入的数据
        SecureInvokeRecord secureInvokeRecord = SecureInvokeRecord.builder()
                .secureInvokeJson(JSONUtil.toJsonStr(secureInvokeDTO))
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .status(SecureInvokeStatusEnum.WAIT.getStatus())
                .retryTimes(0)
                .build();
        secureInvokeRecordService.invoke(secureInvokeRecord, async);
        // 这里并不是真正执行目标方法中的业务, 而是将目标方法的所有信息存到本地消息表中, 使用定时任务来执行目标方法
        // 所以这里直接返回null即可
        return null;
    }
}
