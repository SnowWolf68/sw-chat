package com.snwolf.chat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IdempotentEnum {

    UID(1, "uid"),
    MESSAGE_ID(2, "消息Id")
    ;

    private final Integer type;
    private final String desc;
}
