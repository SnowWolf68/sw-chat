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
public enum RoomFriendStatusEnum {

    NO(0, "正常"),
    YES(1, "禁用(删好友了禁用)"),
    ;

    final Integer code;
    final String desc;
}
