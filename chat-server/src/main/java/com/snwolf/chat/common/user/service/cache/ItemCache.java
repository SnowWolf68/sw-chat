package com.snwolf.chat.common.user.service.cache;

import com.snwolf.chat.common.user.dao.ItemConfigDao;
import com.snwolf.chat.common.user.domain.entity.ItemConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("ItemCache")
public class ItemCache {

    @Resource
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item", key = "'itemByType:' + #itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getByType(itemType);
    }

    @CacheEvict(cacheNames = "item", key = "'itemByType:' + #itemType")
    public void evict(Integer itemType) {
    }
}
