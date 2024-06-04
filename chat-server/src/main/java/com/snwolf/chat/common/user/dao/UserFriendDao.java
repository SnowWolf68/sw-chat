package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.UserFriend;
import com.snwolf.chat.common.user.mapper.UserFriendMapper;
import com.snwolf.chat.common.user.service.UserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-04
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> implements UserFriendService {

}
