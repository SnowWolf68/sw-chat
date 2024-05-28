package com.snwolf.chat.common.websocket.service.adapter;

import com.snwolf.chat.common.websocket.domain.enums.WSRespTypeEnum;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import com.snwolf.chat.common.websocket.domain.vo.response.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {

    public static WSBaseResp<?> build(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> response = new WSBaseResp<>();
        response.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        response.setType(WSRespTypeEnum.LOGIN_URL.getType());
        return response;
    }
}
