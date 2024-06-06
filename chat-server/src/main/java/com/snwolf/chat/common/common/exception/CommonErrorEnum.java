package com.snwolf.chat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum{

    PARAM_INVALID(-2, "参数校验失败"),
    SYSTEM_ERROR(-1, "系统出小差了, 请稍后再试..."),
    BUSINESS_ERROR(0, "{0}"),
    LOCK_LIMIT(-3, "请求太频繁了, 请稍后再试..."),
    HANDLER_NOT_EXIST(-4, "消息处理器不要存在")
    ;

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
