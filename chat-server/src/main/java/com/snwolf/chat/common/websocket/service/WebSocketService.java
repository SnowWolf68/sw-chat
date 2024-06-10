package com.snwolf.chat.common.websocket.service;

import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import com.snwolf.chat.common.websocket.domain.vo.response.WSFriendApply;
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

    /**
     * 给某一个用户id发消息
     *  由于写到这里的时候, 消息模块还没有做, 所以这里先写一个简单实现
     * @param wsFriendApplyWSBaseResp
     * @param targetId
     */
    void sendMsgToTargetId(WSBaseResp<WSFriendApply> wsFriendApplyWSBaseResp, Long targetId);

    void pushMsgToAll(WSBaseResp<?> chatMessageRespWSBaseResp);
}
