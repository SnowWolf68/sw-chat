package com.snwolf.chat.common.chat.service.cache;

import com.snwolf.chat.common.chat.dao.RoomGroupDao;
import com.snwolf.chat.common.chat.domain.entity.RoomGroup;
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
public class RoomGroupCache extends AbstractBatchStringRedisCache<Long, Long> {

    @Resource
    private RoomGroupDao roomGroupDao;


    @Override
    protected String getKey(Long roomId) {
        return RedisKeyConstant.getKey(RedisKeyConstant.ROOM_GROUP_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return RedisTTLConstants.ROOM_GROUP_TTL;
    }

    @Override
    protected Map<Long, Long> load(List<Long> roomIdList) {
        List<RoomGroup> roomGroupList = roomGroupDao.listByRoomIds(roomIdList);
        return roomGroupList.stream()
                .collect(Collectors.toMap(RoomGroup::getRoomId, RoomGroup::getId));
    }
}
