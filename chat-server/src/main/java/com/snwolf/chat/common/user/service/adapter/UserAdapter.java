package com.snwolf.chat.common.user.service.adapter;

import com.snwolf.chat.common.user.domain.entity.User;

public class UserAdapter {

    public static User buildUser(String openId) {
        return User.builder()
                .openId(openId)
                .build();
    }
}
