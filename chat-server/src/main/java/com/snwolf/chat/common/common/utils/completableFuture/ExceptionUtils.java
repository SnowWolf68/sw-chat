package com.snwolf.chat.common.common.utils.completableFuture;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class ExceptionUtils {
    /**
     * 提取真正的异常, 来源自<a href="https://mp.weixin.qq.com/s/GQGidprakfticYnbVYVYGQ">美团技术文章</a>
     */
    public static Throwable extractRealException(Throwable throwable) {
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }
}