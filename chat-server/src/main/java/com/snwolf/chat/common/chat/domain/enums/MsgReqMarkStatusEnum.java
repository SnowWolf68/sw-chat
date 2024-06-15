package com.snwolf.chat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description: 前端传来的消息标记状态枚举
 */
@Getter
@AllArgsConstructor
public enum MsgReqMarkStatusEnum {

    MARK(1, "确认标记"),
    UN_MARK(2, "取消标记"),
    ;

    private final Integer code;
    private final String desc;

    private static Map<Integer, MsgReqMarkStatusEnum> cache;

    static {
        cache = Arrays.stream(MsgReqMarkStatusEnum.values()).collect(Collectors.toMap(MsgReqMarkStatusEnum::getCode, Function.identity()));
    }

    public static MsgReqMarkStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
