package com.snwolf.swchat.transaction.configurer;

import org.springframework.lang.Nullable;

import java.util.concurrent.Executor;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/7/2024
 * @Description:
 */
public interface SecureInvokeConfigurer {

    /**
     * 返回一个线程池, 如果starter的使用者想要自己配置线程池, 可以使用这个接口进行配置
     * <p>如果不进行配置, 那么使用{@code ForkJoinPool.commonPool()}创建默认的线程池
     */
    @Nullable
    default Executor getSecureInvokeExecutor() {
        return null;
    }
}
