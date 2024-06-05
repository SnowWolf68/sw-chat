package com.snwolf.chat.common.user.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.constant.RedisTTLConstants;
import com.snwolf.chat.common.common.utils.RedisUtils;
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
 * @Description:
 */
@Component
public class UserSummaryCache {

    @Resource
    private UserDao userDao;


    /**
     * 使用redis进行缓存
     * <p>查询出ids对应的SummartInfoDTO, 封装成map进行返回
     * <p>使用批量缓存进行优化
     *
     * @param ids
     * @return
     */
    public Map<Long, SummaryInfoDTO> getBatch(List<Long> ids) {
        // 使用ids从redis中查询数据, redisKey: userSummary:uid_20001, 批量查询redis
        List<String> keyList = ids.stream().map(
                id -> RedisKeyConstant.getKey(RedisKeyConstant.USER_SUMMARY_STRING, id)).collect(Collectors.toList());
        List<SummaryInfoDTO> redisResult = RedisUtils.mget(keyList, SummaryInfoDTO.class);
        Map<Long, SummaryInfoDTO> redisResultMap = redisResult.stream()
                .filter(ObjectUtil::isNotNull)
                .collect(Collectors.toMap(SummaryInfoDTO::getUid, summaryInfoDTO -> summaryInfoDTO));
        // 找出哪些数据没有redis缓存, 需要查询数据库
        List<Long> needQueryDBIdList = ids.stream()
                .filter(id -> !redisResultMap.containsKey(id))
                .collect(Collectors.toList());
        // 拿着缺少的数据到数据库中进行查询, 批量查询数据库
        if (CollectionUtil.isNotEmpty(needQueryDBIdList)) {
            // 从数据库中批量获取用户信息
            List<SummaryInfoDTO> loadFromDBList = loadFromDB(needQueryDBIdList);
            // 将这些数据写回redis, redisKey: userSummary:uid_20001
            Map<String, SummaryInfoDTO> loadFromDBMap = loadFromDBList.stream()
                    .collect(Collectors.toMap(
                            userSummaryDTO -> RedisKeyConstant.getKey(RedisKeyConstant.USER_SUMMARY_STRING, userSummaryDTO.getUid()),
                            userSummaryDTO -> userSummaryDTO));
            RedisUtils.mset(loadFromDBMap, RedisTTLConstants.USER_SUMMARY_TTL);
            // 将这些数据封装到返回值中
            redisResultMap.putAll(loadFromDBList.stream().
                    collect(Collectors.toMap(SummaryInfoDTO::getUid, userSummaryDTO -> userSummaryDTO)));
        }
        return redisResultMap;
    }


    /**
     * 从数据库中宏加载ids对应的user数据
     *
     * @param ids
     * @return
     */
    private List<SummaryInfoDTO> loadFromDB(List<Long> ids) {
        List<User> userList = userDao.listByIds(ids);
        return userList.stream()
                .map(SummaryAdapter::buildUserSummary)
                .collect(Collectors.toList());
    }
}
