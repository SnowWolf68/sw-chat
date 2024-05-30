package com.snwolf.chat.common.common.utils;

import com.snwolf.chat.common.common.domain.dto.RequestUserInfo;

public class RequestHolder {

    private static final ThreadLocal<RequestUserInfo> threadLocal = new ThreadLocal<>();

    public static void setUserInfo(RequestUserInfo requestUserInfo){
        threadLocal.set(requestUserInfo);
    }

    public static RequestUserInfo getUserInfo(){
        return threadLocal.get();
    }

    public static void removeUserInfo(){
        threadLocal.remove();
    }
}
