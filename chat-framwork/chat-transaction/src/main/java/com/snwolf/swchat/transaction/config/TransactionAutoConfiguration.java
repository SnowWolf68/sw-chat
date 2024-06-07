package com.snwolf.swchat.transaction.config;

import com.snwolf.swchat.transaction.aspect.SecureInvokeAspect;
import com.snwolf.swchat.transaction.configurer.SecureInvokeConfigurer;
import com.snwolf.swchat.transaction.dao.SecureInvokeRecordDao;
import com.snwolf.swchat.transaction.domain.dto.SecureInvokeDTO;
import com.snwolf.swchat.transaction.mapper.SecureInvokeRecordMapper;
import com.snwolf.swchat.transaction.service.impl.SecureInvokeRecordServiceImpl;
import io.micrometer.core.lang.Nullable;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/7/2024
 * @Description:
 */
@Configuration
@EnableScheduling   // 开始Spring Task定时任务
@MapperScan(basePackageClasses = SecureInvokeRecordMapper.class)
@Import({SecureInvokeAspect.class, SecureInvokeDTO.class})
public class TransactionAutoConfiguration {

    /**
     * 线程池对象, 这个starter的使用者通过实现SecureInvokeConfigurer接口提供
     */
    @Nullable
    private Executor executor;

    /**
     * Collect any {@link SecureInvokeConfigurer} beans through autowiring.
     */
    @Autowired
    void setConfigurers(ObjectProvider<SecureInvokeConfigurer> configurers) {
        Supplier<SecureInvokeConfigurer> configurer = SingletonSupplier.of(() -> {
            List<SecureInvokeConfigurer> candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one SecureInvokeConfigurer may exist");
            }
            return candidates.get(0);
        });
        executor = Optional.ofNullable(configurer.get()).map(SecureInvokeConfigurer::getSecureInvokeExecutor).orElse(ForkJoinPool.commonPool());
    }

    /**
     * 注册service对象, 同时设置executor
     * @param dao
     * @return
     */
    @Bean
    public SecureInvokeRecordServiceImpl getSecureInvokeService(SecureInvokeRecordDao dao) {
        return new SecureInvokeRecordServiceImpl(dao, executor);
    }
}
