package com.snwolf.chat.common.chat.service;

import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
public class MessageAdapter {


    /**
     * 构建消息, 不包含消息体(extra, content)
     * @param uid
     * @param chatMessageReq
     * @return
     */
    public static Message buildMessageWithoutBody(Long uid, ChatMessageReq chatMessageReq) {
        return Message.builder()
                .roomId(chatMessageReq.getRoomId())
                .fromUid(uid)
                .status(StatusEnum.STATUS_INVALID.getStatus())
                .type(chatMessageReq.getMsgType())
                .build();
    }
}
