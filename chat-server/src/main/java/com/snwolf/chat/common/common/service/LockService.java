package com.snwolf.chat.common.common.service;

import com.snwolf.chat.common.common.exception.BusinessException;
import com.snwolf.chat.common.common.exception.CommonErrorEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {

    @Resource
    private RedissonClient redissonClient;

    public <T> T excueteWithLock(String lockKey, int waitTime, TimeUnit timeUnit, Supplier<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(lockKey);
        boolean result = false;
        try {
            result = lock.tryLock(waitTime, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!result) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    public <T> T excueteWithLock(String lockKey, Supplier<T> supplier) throws Throwable {
        return excueteWithLock(lockKey, -1, TimeUnit.SECONDS, supplier);
    }

    public <T> T excueteWithLock(String lockKey, Runnable runnable) throws Throwable {
        return excueteWithLock(lockKey, -1, TimeUnit.SECONDS, () -> {
            runnable.run();
            return null;
        });
    }

    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}
