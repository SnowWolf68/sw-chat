package com.snwolf.chat.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/14/2024
 * @Description:
 */
@Slf4j
public class CompletabltFutureTest4 {

    @Test
    void testException(){
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
            log.info("runAsync");
            throw new RuntimeException();
        });
        cf1.exceptionally(throwable -> {
            log.error("exceptionally, e: " + ExceptionUtils.extractRealException(throwable));
            return null;
        });
    }
}

class ExceptionUtils {
    public static Throwable extractRealException(Throwable throwable) {
        //这里判断异常类型是否为CompletionException、ExecutionException，如果是则进行提取，否则直接返回。
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }
}