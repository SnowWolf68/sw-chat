package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.mapper.UserBackpackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
}
