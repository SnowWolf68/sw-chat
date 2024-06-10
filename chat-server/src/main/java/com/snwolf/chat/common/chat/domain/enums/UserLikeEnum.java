package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum UserLikeEnum {

    NO(0, "否"),
    YES(1, "是")
    ;

    private final Integer code;
    private final String desc;
}
