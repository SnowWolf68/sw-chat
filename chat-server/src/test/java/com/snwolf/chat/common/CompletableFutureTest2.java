package com.snwolf.chat.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/14/2024
 * @Description:
 */
@Slf4j
public class CompletableFutureTest2 {


    /**
     * 自定义线程池
     */
    private ExecutorService executor = Executors.newFixedThreadPool(3);

    @Test
    void testSupplyAsyncWithDefaultThreadPool() throws ExecutionException, InterruptedException {
        // 使用默认线程池
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            return threadName + ": result";
        });
        String result = completableFuture.get();
        log.info(result);
    }

    @Test
    void testSupplyAsyncWithCustomThreadPool() throws ExecutionException, InterruptedException {
        // 使用默认线程池
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            String threadName = Thread.currentThread().getName();
            return threadName + ": result";
        }, executor);
        String result = completableFuture.get();
        log.info(result);
    }

    @Test
    void testThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
            ;
            return "result1";
        });
        CompletableFuture<String> cf2 = cf1.thenApply(s -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            return s + " result2";
        });
        log.info(cf1.get());
        log.info(cf2.get());
    }

    @Test
    void testThenApplyAsyncWithDefaultThreadPool() throws Exception {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
            ;
            return "result1";
        });
        CompletableFuture<String> cf2 = cf1.thenApplyAsync(s -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            return s + " result2";
        });
        log.info(cf1.get());
        log.info(cf2.get());
    }

    @Test
    void testThenApplyAsyncWithCustomThreadPool() throws Exception {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
            return "result1";
        });
        CompletableFuture<String> cf2 = cf1.thenApplyAsync(s -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            return s + " result2";
        }, executor);
        log.info(cf1.get());
        log.info(cf2.get());
    }

    @Test
    void testWhenComplete() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
//            int a = 1 / 0;
            return 1;
        });
        CompletableFuture<Integer> cf2 = cf1.whenCompleteAsync((result, e) -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            log.info("cf1 result: " + result);
            log.info("cf1 exception: " + e);
        });
        // 注意这里cf2.get()得到的是cf1的异常信息, 并且这个异常信息还经过一层封装
        log.info("cf2 result: " + cf2.get());
    }

    @Test
    void testthenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
            return 1;
        });

        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            return 2;
        });

        CompletableFuture<Integer> cf3 = cf1.thenCombineAsync(cf2, (a, b) -> {
            log.info("cf3 is running on thread: " + Thread.currentThread().getName());
            return a + b;
        });

        log.info("cf3 result: " + cf3.get());
    }

    @Test
    void testAllOf() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
            return 1;
        });
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            return 2;
        });
        CompletableFuture<Integer> cf3 = CompletableFuture.supplyAsync(() -> {
            log.info("cf3 is running on thread: " + Thread.currentThread().getName());
            // int a = 1 / 0;
            return 3;
        });
        CompletableFuture<Void> cf4 = CompletableFuture.allOf(cf1, cf2, cf3);
        cf4.get();
    }

    @Test
    void testAnyOf() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            log.info("cf1 is running on thread: " + Thread.currentThread().getName());
            return 1;
        });
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            log.info("cf2 is running on thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 2;
        });
        CompletableFuture<Integer> cf3 = CompletableFuture.supplyAsync(() -> {
            log.info("cf3 is running on thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int a = 1 / 0;
            return 3;
        });
        CompletableFuture<Object> cf4 = CompletableFuture.anyOf(cf1, cf2, cf3);
        log.info("cf4 result: " + cf4.get().toString());
    }

}
