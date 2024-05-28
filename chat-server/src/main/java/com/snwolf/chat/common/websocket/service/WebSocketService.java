package com.snwolf.chat.common.websocket.service;

import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

public interface WebSocketService {


    void connect(Channel channel);

    void handleLoginReq(Channel channel) throws WxErrorException;
}
