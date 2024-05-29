package com.snwolf.chat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("exception in thread:{}, exception:{}", t.getName(), e);
    }
}
