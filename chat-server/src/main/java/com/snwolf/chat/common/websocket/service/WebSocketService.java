package com.snwolf.chat.common.websocket.service;

import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

public interface WebSocketService {


    void connect(Channel channel);

    void handleLoginReq(Channel channel) throws WxErrorException;

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String token);

    void sendMsgToAll(WSBaseResp<?> msg);
}
