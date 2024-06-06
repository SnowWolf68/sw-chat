package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    TEXT(1, "正常消息"),
    RECALL(2, "撤回消息"),
    IMG(3, "图片"),
    FILE(4, "文件"),
    SOUND(5, "语音"),
    VIDEO(6, "视频"),
    EMOJI(7, "表情"),
    SYSTEM(8, "系统消息"),
    ;

    private final Integer type;
    private final String desc;
}
