package com.snwolf.chat.common.user.service.adapter;

import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.user.domain.enums.ApplyReadStatusEnum;
import com.snwolf.chat.common.user.domain.enums.ApplyStatusEnum;
import com.snwolf.chat.common.user.domain.enums.ApplyTypeEnum;
import com.snwolf.chat.common.user.domain.vo.req.FriendApplyReq;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/4/2024
 * @Description:
 */
public class UserApplyAdapter {

    public static UserApply buildApply(Long uid, FriendApplyReq friendApplyReq) {
        return UserApply.builder()
                .uid(uid)
                .targetId(friendApplyReq.getTargetUid())
                .msg(friendApplyReq.getMsg())
                .type(ApplyTypeEnum.ADD_FRIEND.getCode())
                .status(ApplyStatusEnum.WAIT_APPROVAL.getCode())
                .readStatus(ApplyReadStatusEnum.UNREAD.getCode())
                .build();
    }
}
