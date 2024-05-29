package com.snwolf.chat.common.common.constant;

public class RedisKeyConstant {

    public static final String BASE_KEY = "swchat:";

    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    public static String getKey(String key, Object... o){
        return BASE_KEY + String.format(key, o);
    }
}
