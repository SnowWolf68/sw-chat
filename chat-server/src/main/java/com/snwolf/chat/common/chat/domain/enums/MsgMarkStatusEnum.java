package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description: 数据库中的消息状态枚举
 */
@Getter
@AllArgsConstructor
public enum MsgMarkStatusEnum {

    MARK(0, "正常"),
    UN_MARK(1, "取消")
    ;

    private final Integer code;
    private final String desc;
}
