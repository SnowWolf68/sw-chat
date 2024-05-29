package com.snwolf.chat.common;

import com.snwolf.chat.common.common.thread.MyUncaughtExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class ThreadPoolTest {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    void test1() throws InterruptedException {
        threadPoolTaskExecutor.execute(()->{
            RuntimeException e = new RuntimeException("故意的");
            throw e;
        });
        Thread.sleep(200);
    }

    @Test
    void test2() throws InterruptedException {
        Thread thread = new Thread(()->{
            RuntimeException e = new RuntimeException("故意的");
            throw e;
        });
        thread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        thread.start();

        Thread.sleep(200);
    }
}
