package com.snwolf.chat.common;

import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;
import com.snwolf.chat.common.user.service.impl.UserBackpackServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserBackpackServiceImplTest {

    public static final long UID = 20003L;
    @Resource
    private UserBackpackServiceImpl userBackpackServiceImpl;

    @Test
    void test1(){
        userBackpackServiceImpl.distributeItem(UID, 6L, IdempotentEnum.UID, UID + "");
    }

    @Test
    void test2(){
        userBackpackServiceImpl.distributeItem(UID, 5L, IdempotentEnum.UID, UID + "");
    }
}