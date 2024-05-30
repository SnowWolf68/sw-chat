package com.snwolf.chat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.snwolf.chat.common.common.domain.dto.RequestUserInfo;
import com.snwolf.chat.common.common.utils.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.UID))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
        String ip = ServletUtil.getClientIP(request);
        RequestUserInfo requestUserInfo = RequestUserInfo.builder()
                .ip(ip)
                .uid(uid)
                .build();
        RequestHolder.setUserInfo(requestUserInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.removeUserInfo();
    }
}
