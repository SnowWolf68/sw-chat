package com.snwolf.chat.common.user.service;

import com.snwolf.chat.common.user.domain.entity.RoomFriend;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-05
 */
public interface RoomService {

    /**
     * 创建单聊房间
     * @param uids
     * @return
     */
    RoomFriend createFriendRoom(List<Long> uids);
}
