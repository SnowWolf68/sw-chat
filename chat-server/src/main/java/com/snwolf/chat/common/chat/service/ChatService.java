package com.snwolf.chat.common.chat.service;

import com.snwolf.chat.common.chat.domain.entity.ChatMessagePageReq;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;

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


    CursorPageBaseResp<ChatMessageResp> getCursorPage(ChatMessagePageReq request, Long receiveUid);

    ChatMessageResp buildSingleChatMessageResp(Long msgId, Long receiveUid);
}
