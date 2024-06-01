package com.snwolf.chat.common.websocket.service;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NettyUtils {

    public static AttributeKey<String> TOKEN_KEY = AttributeKey.valueOf("token");
    public static AttributeKey<String> IP_KEY = AttributeKey.valueOf("ip");

    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T value){
        channel.attr(key).set(value);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> key){
        return channel.attr(key).get();
    }
}
