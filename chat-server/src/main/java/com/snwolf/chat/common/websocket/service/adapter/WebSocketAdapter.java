package com.snwolf.chat.common.websocket.service.adapter;

import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.ApplyTypeEnum;
import com.snwolf.chat.common.websocket.domain.enums.WSRespTypeEnum;
import com.snwolf.chat.common.websocket.domain.vo.response.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {

    public static WSBaseResp<?> build(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> response = new WSBaseResp<>();
        response.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        response.setType(WSRespTypeEnum.LOGIN_URL.getType());
        return response;
    }

    public static WSBaseResp<?> buildResp(User user, String token, boolean isAdmin) {
        WSBaseResp<WSLoginSuccess> response = new WSBaseResp<>();
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .name(user.getName())
                .avatar(user.getAvatar())
                .token(token)
                .uid(user.getId())
                .power(isAdmin ? StatusEnum.STATUS_VALID.getStatus() : StatusEnum.STATUS_INVALID.getStatus())
                .build();
        response.setData(wsLoginSuccess);
        response.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        return response;
    }

    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<Object> response = new WSBaseResp<>();
        response.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return response;
    }

    public static WSBaseResp<?> buildInvalidResp() {
        WSBaseResp<Object> response = new WSBaseResp<>();
        response.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return response;
    }

    public static WSBaseResp<?> buildBlack(User user) {
        WSBaseResp<WSBlack> blackResp = new WSBaseResp<>();
        blackResp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsBlack = WSBlack.builder()
                .uid(user.getId())
                .build();
        blackResp.setData(wsBlack);
        return blackResp;
    }

    public static WSBaseResp<WSFriendApply> buildUserApplyResp(Long uid, Integer count) {
        WSFriendApply wsFriendApply = WSFriendApply.builder()
                .uid(uid)
                .unreadCount(count)
                .build();
        WSBaseResp<WSFriendApply> response = new WSBaseResp<>();
        response.setType(WSRespTypeEnum.APPLY.getType());
        response.setData(wsFriendApply);
        return response;
    }

    public static WSBaseResp<ChatMessageResp> buildChatMessageResp(ChatMessageResp chatMessageResp, WSRespTypeEnum wsRespTypeEnum) {
        WSBaseResp<ChatMessageResp> response = new WSBaseResp<>();
        response.setType(wsRespTypeEnum.getType());
        response.setData(chatMessageResp);
        return response;
    }
}
