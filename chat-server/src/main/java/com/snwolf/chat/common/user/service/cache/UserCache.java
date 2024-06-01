package com.snwolf.chat.common.user.service.cache;

import com.snwolf.chat.common.user.dao.UserRoleDao;
import com.snwolf.chat.common.user.domain.entity.UserRole;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Resource
    private UserRoleDao userRoleDao;

    @Cacheable(cacheNames = "user", key = "'roles' + #uid")
    public Set<Long> getRoleSetByUid(Long uid) {
        List<UserRole> userRoleList = userRoleDao.listByUid(uid);
        return userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
