package com.snwolf.chat.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Charsets;
import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
@Getter
public enum HttpErrorEnum {
    ACCESS_DENIED(401, "登录失效，请重新登录"),
    ;
    private Integer httpCode;
    private String msg;

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(this.getHttpCode());
        ApiResult responseData = ApiResult.fail(this.getHttpCode(), this.getMsg());
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JSONUtil.toJsonStr(responseData));
    }
}
