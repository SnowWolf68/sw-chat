package com.snwolf.chat.common.user.service.cache.batchCacheWithFramework;

import com.snwolf.chat.common.common.service.cache.AbstractBatchCaffeineCache;
import com.snwolf.chat.common.user.dao.ItemConfigDao;
import com.snwolf.chat.common.user.domain.entity.ItemConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description: 使用Caffeine实现的批量缓存框架完成用户徽章缓存
 */
@Component("BatchFrameworkItemCache")
public class ItemCache extends AbstractBatchCaffeineCache<ItemConfig, Long> {

    @Resource
    private ItemConfigDao itemConfigDao;


    @Override
    protected Map<Long, ItemConfig> load(List<Long> keyList) {
        List<ItemConfig> itemList = itemConfigDao.listByIds(keyList);
        return itemList.stream()
                .collect(Collectors.toMap(ItemConfig::getId, itemConfig -> itemConfig));
    }
}
