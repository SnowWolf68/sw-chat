package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.user.dao.UserBackpackDao;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;
import com.snwolf.chat.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserBackpackService implements IUserBackpackService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Override
    public void distributeItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        String redissonKey = String.format("distributeItem:%s", idempotent);
        RLock lock = redissonClient.getLock(redissonKey);
        boolean result = lock.tryLock();
        AssertUtil.isTrue(result, "请求太频繁了, 休息一下再来吧");
        try {
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
        } finally {
            lock.unlock();
        }
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d:%d:%s", itemId, idempotentEnum.getType(), businessId);
    }
}
