package com.snwolf.chat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {

    ADMIN(1L, "超级管理员"),
    CHAT_MANAGER(2L, "群聊管理员")
    ;

    private final Long id;
    private final String desc;
}
