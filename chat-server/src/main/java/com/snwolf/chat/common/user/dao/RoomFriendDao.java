package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.user.domain.entity.RoomFriend;
import com.snwolf.chat.common.user.mapper.RoomFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.chat.common.user.service.adapter.RoomAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-05
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    public RoomFriend getByRoomKey(String roomKey) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomKey, roomKey)
                .one();
    }

    public void restoreRoomFriend(Long id) {
        lambdaUpdate()
                .eq(RoomFriend::getId, id)
                .set(RoomFriend::getStatus, StatusEnum.STATUS_VALID.getStatus())
                .update();
    }


    public RoomFriend createRoomFriend(Long id, List<Long> uids) {
        RoomFriend roomFriend = RoomAdapter.buildRoom(id, uids);
        save(roomFriend);
        return roomFriend;
    }
}
