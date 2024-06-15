package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum MsgMarkTypeEnum {

    LIKE(1, "点赞", 10),
    DISLIKE(2, "点踩", -1)
    ;

    private final Integer code;
    private final String desc;
    /**
     * 一条消息点赞超过10个给对应用户发一个徽章
     * <br>点踩消息的count暂时没有作用
     */
    private final Integer count;
}
