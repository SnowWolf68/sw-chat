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
 * @Date: 6/8/2024
 * @Description: 是否是全员群 枚举类
 */
@Getter
@AllArgsConstructor
public enum HotFlagEnum {

    NO(0, "否"),
    YES(1, "是");

    private final Integer code;
    private final String desc;

    private static Map<Integer, HotFlagEnum> cache;

    static {
        cache = Arrays.stream(HotFlagEnum.values()).collect(Collectors.toMap(HotFlagEnum::getCode, Function.identity()));
    }

    public static HotFlagEnum of(Integer type) {
        return cache.get(type);
    }
}
