package com.snwolf.chat.common.user.service;

import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-29
 */
public interface IUserBackpackService {

    /**
     * 给用户发放一个物品
     * @param uid: 用户id
     * @param itemId: 物品id
     * @param idempotentEnum: 幂等类型
     * @param businessId: 幂等唯一标识
     */
    public void distributeItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
