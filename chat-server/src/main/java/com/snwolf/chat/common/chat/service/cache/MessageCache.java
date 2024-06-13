package com.snwolf.chat.common.chat.service.cache;

import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description:
 */
@Component
public class MessageCache {

    @Resource
    private MessageDao messageDao;


    @Cacheable(cacheNames = "swchat", key = "'message:messageId_' + #msgId", cacheManager = "caffeineCacheManager")
    public Message getMessage(Long msgId) {
        return messageDao.getById(msgId);
    }

    @CacheEvict(cacheNames = "swchat", key = "'message:messageId_' + #msgId", cacheManager = "caffeineCacheManager")
    public void evict(Long msgId) {
    }
}
