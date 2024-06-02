package com.snwolf.chat.common.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.exception.HttpErrorEnum;
import com.snwolf.chat.common.common.utils.RequestHolder;
import com.snwolf.chat.common.user.domain.enums.BlackTypeEnum;
import com.snwolf.chat.common.user.service.cache.UserCache;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

@Component
public class BlackInterceptor implements HandlerInterceptor {

    @Resource
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        if(inBlackList(RequestHolder.getUserInfo().getUid(), blackMap.get(BlackTypeEnum.UID.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        if(inBlackList(RequestHolder.getUserInfo().getIp(), blackMap.get(BlackTypeEnum.IP.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> targetSet) {
        if(ObjectUtil.isNull(target) || CollectionUtil.isEmpty(targetSet)){
            return false;
        }
        return targetSet.contains(target.toString());
    }
}
