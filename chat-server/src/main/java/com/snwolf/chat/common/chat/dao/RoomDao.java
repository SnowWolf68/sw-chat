package com.snwolf.chat.common.chat.dao;

import com.snwolf.chat.common.chat.domain.entity.Room;
import com.snwolf.chat.common.chat.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.chat.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.chat.common.user.service.adapter.RoomAdapter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-05
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

    public Room createRoom(RoomTypeEnum roomTypeEnum) {
        Room room = RoomAdapter.buildRoomByType(roomTypeEnum);
        save(room);
        return room;
    }

    public void updateActiveTimeAndLastMsgId(Long id, Long msgId, LocalDateTime createTime) {
        lambdaUpdate()
                .eq(Room::getId, id)
                // todo: 使用乐观锁保证 哪怕收到消息的顺序和发送不一致, 这里的activeTime也不会更新的比原来更小
                .set(Room::getActiveTime, createTime)
                .set(Room::getLastMsgId, msgId)
                .update();
    }
}
