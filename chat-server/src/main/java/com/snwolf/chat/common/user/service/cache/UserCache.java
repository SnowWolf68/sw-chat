package com.snwolf.chat.common.user.service.cache;

import com.snwolf.chat.common.user.dao.BlackDao;
import com.snwolf.chat.common.user.dao.UserRoleDao;
import com.snwolf.chat.common.user.domain.entity.Black;
import com.snwolf.chat.common.user.domain.entity.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private BlackDao blackDao;

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
}
