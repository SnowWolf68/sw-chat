package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description: 用户是否举报
 */
@Getter
@AllArgsConstructor
public enum UserDislikeEnum {

    NO(0, "未举报"),
    YES(1, "已举报");

    private final Integer code;
    private final String desc;
}
