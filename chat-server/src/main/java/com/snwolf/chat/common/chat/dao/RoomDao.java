package com.snwolf.chat.common.chat.dao;

import com.snwolf.chat.common.chat.domain.entity.Room;
import com.snwolf.chat.common.chat.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.chat.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.chat.common.user.service.adapter.RoomAdapter;
import org.springframework.stereotype.Service;

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
}
