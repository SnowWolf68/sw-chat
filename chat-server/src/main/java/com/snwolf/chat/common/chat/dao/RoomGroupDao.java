package com.snwolf.chat.common.chat.dao;

import com.snwolf.chat.common.chat.domain.entity.RoomGroup;
import com.snwolf.chat.common.chat.mapper.RoomGroupMapper;
import com.snwolf.chat.common.chat.service.IRoomGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-05
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> implements IRoomGroupService {

    public List<RoomGroup> listByRoomIds(List<Long> roomIdList) {
        return lambdaQuery()
                .in(RoomGroup::getRoomId, roomIdList)
                .list();
    }
}
