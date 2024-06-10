package com.snwolf.chat.common.common.constant;

public class RedisKeyConstant {

    public static final String BASE_KEY = "swchat:";

    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    public static final String USER_LAST_MODIFY_TIME_STRING = "userLastModifyTime:uid_%d";

    public static final String USER_SUMMARY_STRING = "userSummary:uid_%d";

    public static final String ROOM_INFO_STRING = "roomInfo:roomId_%d";

    public static final String ROOM_GROUP_STRING = "roomGroup:roomId_%d";

    public static final String ROOM_ACTIVE_TIME_STRING = "roomActiveTime";

    public static final String USER_INFO_STRING = "userInfo:userId_%d";

    public static String getKey(String key, Object... o){
        return BASE_KEY + String.format(key, o);
    }
}
