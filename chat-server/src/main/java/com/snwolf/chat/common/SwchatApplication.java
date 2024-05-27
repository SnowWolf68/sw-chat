package com.snwolf.chat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.snwolf.chat.common.**.mapper")
public class SwchatApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwchatApplication.class, args);
    }
}
