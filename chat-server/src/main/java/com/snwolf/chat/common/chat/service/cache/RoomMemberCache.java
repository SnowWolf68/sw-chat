package com.snwolf.chat.common.chat.service.cache;

import com.snwolf.chat.common.chat.dao.GroupMemberDao;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/10/2024
 * @Description:
 */
@Component
public class RoomMemberCache {

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberDao groupMemberDao;


    /**
     * 获取房间中的所有成员, 使用Redis缓存
     * @param roomId
     * @return
     */
//    @Cacheable(cacheNames = "roomMember", key = "'roomMember:' + #roomId", cacheManager = "redisCacheManager")
    public List<Long> getMemberIdList(Long roomId){
        Long groupId = roomGroupCache.get(roomId);
        return groupMemberDao.getMemberIdsByGroupId(groupId);
    }

    @CacheEvict(cacheNames = "roomMember", key = "'roomMember:' + #roomId", cacheManager = "redisCacheManager")
    public void evict(Long roomId){}
}
