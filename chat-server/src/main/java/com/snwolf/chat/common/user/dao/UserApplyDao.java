package com.snwolf.chat.common.user.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.user.domain.enums.ApplyReadStatusEnum;
import com.snwolf.chat.common.user.domain.enums.ApplyStatusEnum;
import com.snwolf.chat.common.user.domain.enums.ApplyTypeEnum;
import com.snwolf.chat.common.user.mapper.UserApplyMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Integer getUnReadCount(Long targetId) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getCode())
                .count();
    }

    public Page<UserApply> friendApplyPage(Long uid, Integer pageNo, Integer pageSize) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND)
                .orderByDesc(UserApply::getCreateTime)
                .page(new Page<>(pageNo, pageSize));
    }

    public void markReadByIds(List<Long> ids) {
        lambdaUpdate()
                .set(UserApply::getReadStatus, ApplyReadStatusEnum.READ.getCode())
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UNREAD.getCode())
                .in(UserApply::getId, ids)
                .update();
    }

    public void approve(Long id) {
        lambdaUpdate()
                .eq(UserApply::getId, id)
                .set(UserApply::getStatus, ApplyStatusEnum.AGREE.getCode())
                .update();
    }
}
