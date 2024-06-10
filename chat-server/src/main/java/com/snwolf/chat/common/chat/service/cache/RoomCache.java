package com.snwolf.chat.common.chat.service.cache;

import com.snwolf.chat.common.chat.dao.RoomDao;
import com.snwolf.chat.common.chat.domain.entity.Room;
import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.constant.RedisTTLConstants;
import com.snwolf.chat.common.common.service.cache.AbstractBatchStringRedisCache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Component
public class RoomCache extends AbstractBatchStringRedisCache<Room, Long> {

    @Resource
    private RoomDao roomDao;

    @Override
    protected String getKey(Long roomId) {
        return RedisKeyConstant.getKey(RedisKeyConstant.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return RedisTTLConstants.ROOM_INFO_TTL;
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIdList) {
        List<Room> roomList = roomDao.listByIds(roomIdList);
        return roomList.stream()
                .collect(Collectors.toMap(Room::getId, room -> room));
    }
}
