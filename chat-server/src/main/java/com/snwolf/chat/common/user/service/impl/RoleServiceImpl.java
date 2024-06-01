package com.snwolf.chat.common.user.service.impl;

import com.snwolf.chat.common.user.domain.enums.RoleEnum;
import com.snwolf.chat.common.user.service.RoleService;
import com.snwolf.chat.common.user.service.cache.UserCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSetByUid(uid);
        return roleSet.contains(roleEnum.getId()) || isAdmin(roleSet);
    }

    private boolean isAdmin(Set<Long> roleSet){
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}
