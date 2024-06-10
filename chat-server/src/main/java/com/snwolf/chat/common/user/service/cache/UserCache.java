package com.snwolf.chat.common.user.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.constant.RedisTTLConstants;
import com.snwolf.chat.common.common.service.cache.AbstractBatchStringRedisCache;
import com.snwolf.chat.common.common.utils.RedisUtils;
import com.snwolf.chat.common.user.dao.BlackDao;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.dao.UserRoleDao;
import com.snwolf.chat.common.user.domain.entity.Black;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserCache extends AbstractBatchStringRedisCache<User, Long> {

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private BlackDao blackDao;

    @Resource
    private UserDao userDao;

    /**
     * 从redis中获取ids对应的用户的lastModifyTime
     * @param ids
     * @return
     */
    public static List<Long> getLastModifyTimeByIds(List<Long> ids) {
        // 获取ids集合在redis中对应的Key
        List<String> keys = ids.stream()
                .map(id -> RedisKeyConstant.getKey(RedisKeyConstant.USER_LAST_MODIFY_TIME_STRING, id))
                .collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }

    @Cacheable(cacheNames = "user", key = "'roles' + #uid")
    public Set<Long> getRoleSetByUid(Long uid) {
        List<UserRole> userRoleList = userRoleDao.listByUid(uid);
        return userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * @return: key: Integer: 拉黑的类型(uid, ip)
     * value: Set<String> 具体拉黑的uid或ip的集合
     */
    @Cacheable(cacheNames = "user", key = "'userBlackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> listMap = blackDao.list()
                .stream()
                .collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        listMap.forEach((type, list) -> result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet())));
        return result;
    }

    @CacheEvict(cacheNames = "user", key = "'userBlackList'")
    public Map<Integer, Set<String>> evicateBlackMap() {
        return null;
    }


    public User getUserInfo(Long uid) {
        return CollectionUtil.getFirst(getUserInfoBatch(Collections.singletonList(uid)));
    }

    /**
     * 使用批量缓存框架实现用户信息的缓存
     * todo: 用户信息更新的时候清除缓存
     * @param uidList
     * @return
     */
    public List<User> getUserInfoBatch(List<Long> uidList){
        Map<Long, User> userMap = getBatch(uidList);
        return uidList.stream()
                .map(uid -> userMap.getOrDefault(uid, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    protected String getKey(Long uid) {
        return RedisKeyConstant.getKey(RedisKeyConstant.USER_INFO_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return RedisTTLConstants.USER_INFO_TTL;
    }

    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        List<User> userList = userDao.listByIds(uidList);
        return userList.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }
}
