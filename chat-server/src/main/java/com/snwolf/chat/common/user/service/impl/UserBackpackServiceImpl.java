package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.common.service.LockService;
import com.snwolf.chat.common.user.dao.UserBackpackDao;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;
import com.snwolf.chat.common.user.service.IUserBackpackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

    @Resource
    private LockService lockService;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Override
    public void distributeItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        String redissonKey = String.format("distributeItem:%s", idempotent);

        lockService.excueteWithLock(redissonKey, () -> {
            UserBackpack userBackpack = userBackpackDao.getByIdempotent(uid, itemId, idempotent);
            if(ObjectUtil.isNotNull(userBackpack)){
                // 之前获取过, 直接返回
                return;
            }
            // 这是第一次发放
            // todo: 业务检查
            // 发放物品
            UserBackpack newUserBackPack = UserBackpack.builder()
                    .uid(uid)
                    .itemId(itemId)
                    .idempotent(idempotent)
                    .status(StatusEnum.STATUS_INVALID.getStatus())
                    .build();
            userBackpackDao.save(newUserBackPack);
        });
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d:%d:%s", itemId, idempotentEnum.getType(), businessId);
    }
}
