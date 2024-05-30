package com.snwolf.chat.common.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.exception.HttpErrorEnum;
import com.snwolf.chat.common.user.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String UID = "uid";

    @Resource
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long uid = loginService.getValidUid(token);
        if (ObjectUtil.isNotNull(uid)) {
            // 用户已登录
            request.setAttribute(UID, uid);
            return true;
        } else {
            // 用户未登录, 判断是否是public接口, 如果不是, 返回401
            boolean isPublicURI = isPublicURI(request);
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
            return true;
        }
    }

    private static boolean isPublicURI(HttpServletRequest request) {
        String[] split = request.getRequestURI().split("/");
        return split.length > 4 && "public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        // header有可能为空, 使用Optional来写
        return Optional.ofNullable(header)
                .filter(s -> s.startsWith(AUTHORIZATION_SCHEMA))
                .map(s -> s.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }
}
