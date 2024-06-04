package com.snwolf.chat.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snwolf.chat.common.user.dao.UserFriendDao;
import com.snwolf.chat.common.user.domain.entity.UserFriend;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class PageQueryTest {

    @Resource
    private UserFriendDao userFriendDao;

    @Test
    void test1(){
//        Page<UserFriend> page = userFriendDao.lambdaQuery()
//                .eq(UserFriend::getUid, 20031L)
//                .page(new Page<>(1, 3));
//        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
//        wrapper.eq("uid", 20031L);
        Page<UserFriend> page = userFriendDao.page(new Page<>(1, 3));
        log.info(page.getRecords().size() + "");
    }
}
