package com.snwolf.chat.common.common.constant;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
public class MQConstant {

    /**
     * 发送消息的mq
     */
    public static final String SEND_MSG_TOPIC = "chat_send_msg";
    public static final String SEND_MSG_GROUP = "chat_send_msg_group";

    /**
     * 推送消息的mq
     */
    public static final String PUSH_MSG_TOPIC = "chat_push_msg";
    public static final String PUSH_MSG_GROUP = "chat_push_msg_group";
}
