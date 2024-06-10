package com.snwolf.chat.common.chat.service;

import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 聊天接口service层
 */
public interface ChatService {

    /**
     * 发送消息
     * @param uid: 发送人uid
     * @param chatMessageReq: 发送消息的消息体
     */
    Long sendMsg(Long uid, ChatMessageReq chatMessageReq);

    /**
     * 构建给前端的消息返回对象
     * @param msgId
     * @param receiveUid: 接收消息的uid
     * @return
     */
    ChatMessageResp buildChatMessageResp(Long msgId, Long receiveUid);
}
