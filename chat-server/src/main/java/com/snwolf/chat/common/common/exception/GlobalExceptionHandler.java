package com.snwolf.chat.common.common.exception;

import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder builder = new StringBuilder();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> builder.append("参数名称: ")
                        .append(fieldError.getField())
                        .append(", 校验失败原因: ")
                        .append(fieldError.getDefaultMessage())
                        .append(", "));
        String errMsg = builder.toString();
        errMsg = errMsg.substring(0, errMsg.length() - 1);
        log.error("参数校验失败: {}", errMsg);
        return ApiResult.fail(CommonErrorEnum.PARAM_INVALID.getCode(), errMsg);
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResult<?> handleBusinessException(BusinessException e) {
        log.info("业务异常: ", e);
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }
}
