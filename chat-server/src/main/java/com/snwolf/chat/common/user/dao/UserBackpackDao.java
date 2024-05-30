package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.mapper.UserBackpackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-29
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    public Integer getCountByValidItemId(Long uid, Integer itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, StatusEnum.STATUS_INVALID)
                .count();
    }

    public UserBackpack getFirstUnusedItem(Long uid, Integer itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, StatusEnum.STATUS_INVALID)
                .orderByAsc(UserBackpack::getCreateTime)
                .last("limit 1")
                .one();
    }

    public boolean useItem(UserBackpack modifyNameItem) {
        return lambdaUpdate()
                .eq(UserBackpack::getId, modifyNameItem.getId())
                .eq(UserBackpack::getStatus, StatusEnum.STATUS_INVALID.getStatus())
                .set(UserBackpack::getStatus, StatusEnum.STATUS_VALID.getStatus())
                .update();
    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> ids) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getStatus, StatusEnum.STATUS_INVALID.getStatus())
                .in(UserBackpack::getItemId, ids)
                .list();
    }

    public UserBackpack getFirstItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .orderByAsc(UserBackpack::getCreateTime)
                .last("limit 1")
                .one();
    }
}