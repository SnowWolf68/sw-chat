package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.user.domain.enums.ApplyStatusEnum;
import com.snwolf.chat.common.user.mapper.UserApplyMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-04
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

    public UserApply getByUidAndFriendUid(Long uid, Long targetId) {
        return lambdaQuery()
                .eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVAL.getCode())
                .one();
    }
}
