package com.snwolf.chat.common.chat.service.adapter;

import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.enums.UserDislikeEnum;
import com.snwolf.chat.common.chat.domain.enums.UserLikeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import com.snwolf.chat.common.websocket.service.adapter.WebSocketAdapter;

import java.time.LocalDateTime;

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

    public static ChatMessageResp buildChatMessageResp(Long uid, ChatMessageReq chatMessageReq, Long msgId) {
        ChatMessageResp.MessageMark messageMark = ChatMessageResp.MessageMark.builder()
                .likeCount(0)
                .userLike(UserLikeEnum.NO.getCode())
                .dislikeCount(0)
                .userDislike(UserDislikeEnum.NO.getCode())
                .build();
        ChatMessageResp.Message message = ChatMessageResp.Message.builder()
                .id(msgId)
                .roomId(chatMessageReq.getRoomId())
                .sendTime(LocalDateTime.now())
                .type(chatMessageReq.getMsgType())
                .body(chatMessageReq.getBody())
                .messageMark(messageMark)
                .build();
        ChatMessageResp.UserInfo userInfo = ChatMessageResp.UserInfo.builder()
                .uid(uid)
                .build();
        return ChatMessageResp.builder()
                .fromUser(userInfo)
                .message(message)
                .build();
    }

    public static WSBaseResp<ChatMessageResp> buildWSBaseResp(Message message){
        ChatMessageResp.UserInfo fromUser = ChatMessageResp.UserInfo.builder()
                .uid(message.getFromUid())
                .build();
        ChatMessageResp.Message respMsg = ChatMessageResp.Message.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .sendTime(message.getCreateTime())
                .type(message.getType())
                .body(message.getContent())
                .messageMark(ChatMessageResp.MessageMark.builder()
                        .likeCount(0)
                        .userLike(UserLikeEnum.NO.getCode())
                        .dislikeCount(0)
                        .userDislike(UserDislikeEnum.NO.getCode())
                        .build())
                .build();
        ChatMessageResp chatMessageResp = ChatMessageResp.builder()
                .fromUser(fromUser)
                .message(respMsg)
                .build();
        return WebSocketAdapter.buildChatMessageResp(chatMessageResp);
    }
}
