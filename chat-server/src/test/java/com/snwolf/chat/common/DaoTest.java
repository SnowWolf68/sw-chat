package com.snwolf.chat.common;

import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class DaoTest {

    @Resource
    private UserDao userDao;

    @Test
    void test(){
        User user = userDao.getById(1L);
        log.info("user:{}",user);
        User newUser = User.builder()
                .name("zhangsan")
                .openId("123")
                .build();
        userDao.save(newUser);
        User getUser = userDao.lambdaQuery()
                .eq(User::getName, "zhangsan")
                .one();
        log.info("getUser:{}",getUser);
    }
}
