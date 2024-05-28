package com.snwolf.chat.common.user.service.impl;

import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    @Transactional
    public Long register(User user) {
        userDao.save(user);
        return user.getId();
    }
}
