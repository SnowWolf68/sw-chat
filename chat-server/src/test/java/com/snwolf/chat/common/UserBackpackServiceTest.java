package com.snwolf.chat.common;

import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;
import com.snwolf.chat.common.user.service.impl.UserBackpackService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserBackpackServiceTest {

    public static final long UID = 20003L;
    @Resource
    private UserBackpackService userBackpackService;

    @Test
    void test(){
        userBackpackService.distributeItem(UID, 6L, IdempotentEnum.UID, UID + "");
    }
}