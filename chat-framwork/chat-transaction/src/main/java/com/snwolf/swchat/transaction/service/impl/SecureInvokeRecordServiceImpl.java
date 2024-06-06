package com.snwolf.swchat.transaction.service.impl;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.snwolf.swchat.transaction.dao.SecureInvokeRecordDao;
import com.snwolf.swchat.transaction.domain.SecureInvokeStatusEnum;
import com.snwolf.swchat.transaction.domain.dto.SecureInvokeDTO;
import com.snwolf.swchat.transaction.domain.entity.SecureInvokeRecord;
import com.snwolf.swchat.transaction.service.SecureInvokeRecordService;
import com.snwolf.swchat.transaction.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Service
@Slf4j
@AllArgsConstructor
public class SecureInvokeRecordServiceImpl implements SecureInvokeRecordService {

    public static final double RETRY_INTERVAL_MINUTES = 2D;
    @Resource
    private SecureInvokeRecordDao secureInvokeRecordDao;

    /**
     * 异步调用目标方法所需要的线程池, 在构造方法中传入
     */
    private final Executor executor;

    @Override
    public void invoke(SecureInvokeRecord secureInvokeRecord, boolean async) {
        // 由于这是一个单独的方法, 可以单独调用, 因此这里再一次判断当前线程是否存在事务
        boolean isInTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        if (!isInTransaction) {
            // 当前线程中不存在事务, 无需单独调用目标方法, 直接返回
            return;
        }
        // 当前线程中存在事务
        // 保存调用记录record
        secureInvokeRecordDao.save(secureInvokeRecord);
        // 为了保证调用的及时性, 第一次保存记录之后, 立刻异步调用一次目标方法
        // 给当前事务添加一个同步回调, 在当前事务提交之后, 立即异步调用一次目标方法
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (async) {
                    // 异步调用
                    doAsyncInvoke(secureInvokeRecord);
                } else {
                    // 同步调用
                    doInvoke(secureInvokeRecord);
                }
            }
        });
    }

    private void doInvoke(SecureInvokeRecord record) {
        SecureInvokeDTO secureInvokeDTO = JSONUtil.toBean(record.getSecureInvokeJson(), SecureInvokeDTO.class);
        // 尝试调用目标方法
        try {
            // 从dto中取出目标方法的信息, 反射得到目标类, 目标方法, 进行调用
            Class<?> beanClass = Class.forName(secureInvokeDTO.getClassName());
            // 为了方便, 我们规定目标方法所在类必须被Spring管理, 这样我们可以从Spring容器中根据目标类的Class, 直接取出目标类对象
            Object bean = SpringUtil.getBean(beanClass);
            // 获取目标方法参数类型列表
            List<String> parameterTypesStr = JSONUtil.toList(secureInvokeDTO.getParameterTypes(), String.class);
            // 将String类型的参数类型转换成具体的Class
            List<Class<?>> parameterTypesClass = getParameterTypesClass(parameterTypesStr);
            // 拿着参数列表和方法名, 从目标方法所在类中, 通过反射拿到目标方法的Method对象
            Method method = ReflectUtil.getMethod(beanClass, secureInvokeDTO.getMethodName(), parameterTypesClass.toArray(new Class[]{}));
            // 获得调用目标方法所需要的参数列表
            Object[] args = getArgs(secureInvokeDTO.getArgs(), parameterTypesClass);
            // 执行目标方法
            method.invoke(bean, args);
            // 如果执行成功, 更新执行状态, 直接删除本地消息表中的记录
            removeRecord(record.getId());
        } catch (Exception e) {
            log.error("SecureInvokeService invoke fail: ", e);
            // 重试
            retryRecord(record, e.getMessage());
        }
    }

    private void removeRecord(Long id) {
        secureInvokeRecordDao.removeById(id);
    }

    /**
     * 注意: 该方法中只是更新record的信息, 而不是真正重新调用目标方法
     * <p>调用目标方法重试的工作是由定时任务完成的
     *
     * @param record
     * @param errorMessage
     */
    private void retryRecord(SecureInvokeRecord record, String errorMessage) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord updateRecord = new SecureInvokeRecord();
        updateRecord.setId(record.getId());
        updateRecord.setFailReason(errorMessage);
        updateRecord.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()) {
            // 超过最大重试次数, 无需重试, 直接将失败的信息记录在本地消息表中
            updateRecord.setStatus(SecureInvokeStatusEnum.FAILED.getStatus());
        } else {
            // 没超过最大重试次数, 将更新后的record信息更新到本地消息表中
            updateRecord.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDao.updateById(updateRecord);
    }

    /**
     *
     * 获取下一次重试的时间, 指数退避策略
     * @param retryTimes
     * @return
     */
    private LocalDateTime getNextRetryTime(Integer retryTimes) {
        int waitMinutes = (int) Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);
        return LocalDateTime.now().plusMinutes(waitMinutes);
    }

    /**
     * 将参数列表字符串转换为具体类型的参数列表
     * @param argsStr
     * @param parameterTypesClass
     * @return
     */
    private Object[] getArgs(String argsStr, List<Class<?>> parameterTypesClass) {
        JsonNode jsonNode = JsonUtils.toJsonNode(argsStr);
        Object[] args = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Class<?> aClass = parameterTypesClass.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return args;
    }

    private List<Class<?>> getParameterTypesClass(List<String> parameterTypesStr) {
        return parameterTypesStr.stream()
                .map(str -> {
                    try {
                        return Class.forName(str);
                    } catch (ClassNotFoundException e) {
                        log.error("SecureInvokeService class not found: ", e);
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private void doAsyncInvoke(SecureInvokeRecord record) {
        executor.execute(() -> {
            log.info(Thread.currentThread().getName());
            doInvoke(record);
        });
    }
}
