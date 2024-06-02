package com.snwolf.chat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BlackTypeEnum {

    UID(1, "UID"),
    IP(2, "IP")
    ;

    private final Integer type;
    private final String desc;
}
