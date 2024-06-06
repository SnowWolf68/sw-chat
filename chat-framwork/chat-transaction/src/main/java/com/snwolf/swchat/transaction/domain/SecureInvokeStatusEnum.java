package com.snwolf.swchat.transaction.domain;

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
public enum SecureInvokeStatusEnum {

    WAIT(1, "待执行"),
    FAILED(2, "已失败")
    ;

    private final Integer status;
    private final String desc;
}
