package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum WSPushTypeEnum {

    USER(1, "个人"),
    ALL(2, "所有在线用户")
    ;

    private final Integer type;
    private final String desc;
}
