package com.snwolf.chat.common;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/14/2024
 * @Description:
 */
public class CompletableFutureTest3 {

    @FunctionalInterface
    public interface ThriftAsyncCall {
        void invoke() throws Exception;
    }

    public interface OctoThriftCallback<R, T> {
        void addObserver(OctoObserver<T> octoObserver);
    }

    public interface OctoObserver<T> {
        void onSuccess(T t);
        void onFailure(Throwable throwable );
    }

    /**
     * 该方法为美团内部rpc注册监听的封装，可以作为其他实现的参照
     * OctoThriftCallback 为thrift回调方法
     * ThriftAsyncCall 为自定义函数，用来表示一次thrift调用（定义如上）
     */
    public static <T> CompletableFuture<T> toCompletableFuture(final OctoThriftCallback<?,T> callback , ThriftAsyncCall thriftCall) {
        //新建一个未完成的CompletableFuture
        CompletableFuture<T> resultFuture = new CompletableFuture<>();
        //监听回调的完成，并且与CompletableFuture同步状态
        callback.addObserver(new OctoObserver<T>() {
            @Override
            public void onSuccess(T t) {
                resultFuture.complete(t);
            }
            @Override
            public void onFailure(Throwable throwable) {
                resultFuture.completeExceptionally(throwable);
            }
        });
        if (thriftCall != null) {
            try {
                thriftCall.invoke();
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        }
        return resultFuture;
    }


    @Test
    void test(){
        // todo
    }
}
