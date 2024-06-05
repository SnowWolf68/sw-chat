package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.RoomFriend;
import com.snwolf.chat.common.user.mapper.RoomFriendMapper;
import com.snwolf.chat.common.user.service.IRoomFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-05
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> implements IRoomFriendService {

}