package com.snwolf.chat.common;

import com.snwolf.chat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class TestLoginService {

    @Resource
    private LoginService loginService;

    @Test
    void test(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjIwMDAzLCJjcmVhdGVUaW1lIjoxNzE2OTUzMDM2fQ._TEWJ31e3cCdvN9WQIRkatwNEp-qKTHh9k-JFO55QlI";
        Long validUid = loginService.getValidUid(token);
        log.info(String.valueOf(validUid));
    }
}
