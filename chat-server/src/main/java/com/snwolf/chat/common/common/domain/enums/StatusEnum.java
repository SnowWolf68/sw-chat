package com.snwolf.chat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum {

    STATUS_INVALID(0, "否"),
    STATUS_VALID(1, "是");
    ;

    private final Integer status;
    private final String desc;
}
