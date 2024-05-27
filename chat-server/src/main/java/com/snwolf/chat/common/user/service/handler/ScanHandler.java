package com.snwolf.chat.common.user.service.handler;

import com.snwolf.chat.common.user.service.adapter.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;

@Component
@Slf4j
public class ScanHandler extends AbstractHandler {


//    @Autowired
//    private WxMsgService wxMsgService;

    @Value("${swchat.wx.callback}")
    private String callback;

    private final String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 扫码事件处理
//        return wxMsgService.scan(wxMpService, wxMpXmlMessage);
        String eventKey = wxMpXmlMessage.getEventKey();
        String openId = wxMpXmlMessage.getFromUser();
        String authUrl = String.format(url, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录: <a href=\"" + authUrl + "\">登录</a>", wxMpXmlMessage);

    }

}
