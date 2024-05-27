package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.mapper.UserMapper;
import com.snwolf.chat.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-27
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

}
