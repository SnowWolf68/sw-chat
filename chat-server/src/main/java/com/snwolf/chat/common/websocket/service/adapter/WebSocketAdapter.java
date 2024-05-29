package com.snwolf.chat.common.websocket.service.adapter;

import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.websocket.domain.enums.WSRespTypeEnum;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import com.snwolf.chat.common.websocket.domain.vo.response.WSLoginSuccess;
import com.snwolf.chat.common.websocket.domain.vo.response.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {

    public static WSBaseResp<?> build(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> response = new WSBaseResp<>();
        response.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        response.setType(WSRespTypeEnum.LOGIN_URL.getType());
        return response;
    }

    public static WSBaseResp<?> buildResp(User user, String token) {
        WSBaseResp<WSLoginSuccess> response = new WSBaseResp<>();
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .name(user.getName())
                .avatar(user.getAvatar())
                .token(token)
                .uid(user.getId())
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
}
