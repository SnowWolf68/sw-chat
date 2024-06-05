package com.snwolf.chat.common.user.service.adapter;

import com.snwolf.chat.common.user.domain.entity.UserFriend;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
public class UserFriendAdapter {


    public static UserFriend createFriend(Long uid, Long targetId) {
        return UserFriend.builder()
                .uid(uid)
                .friendUid(targetId)
                .build();
    }
}
