package com.snwolf.chat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/4/2024
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum ApplyTypeEnum {

    ADD_FRIEND(1, "加好友");

    private final Integer code;

    private final String desc;
}
