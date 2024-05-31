package com.snwolf.chat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.snwolf.chat.common.**.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SwchatApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwchatApplication.class, args);
    }
}
