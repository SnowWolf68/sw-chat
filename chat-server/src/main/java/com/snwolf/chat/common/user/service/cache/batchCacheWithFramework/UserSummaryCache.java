package com.snwolf.chat.common.user.service.cache.batchCacheWithFramework;

import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.constant.RedisTTLConstants;
import com.snwolf.chat.common.common.service.cache.AbstractBatchStringRedisCache;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.dto.SummaryInfoDTO;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.service.adapter.SummaryAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description: 使用批量缓存框架实现的UserSummaryCache
 */
@Component
public class UserSummaryCache extends AbstractBatchStringRedisCache<SummaryInfoDTO, Long> {

    @Resource
    private UserDao userDao;


    @Override
    protected String getKey(Long uid) {
        return RedisKeyConstant.getKey(RedisKeyConstant.USER_SUMMARY_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return RedisTTLConstants.USER_SUMMARY_TTL;
    }

    @Override
    protected Map<Long, SummaryInfoDTO> load(List<Long> uidList) {
        List<User> userList = userDao.listByIds(uidList);
        return userList.stream()
                .collect(Collectors.toMap(user -> user.getId(), SummaryAdapter::buildUserSummary));
    }
}
