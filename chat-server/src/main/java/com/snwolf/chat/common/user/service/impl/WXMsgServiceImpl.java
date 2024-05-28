package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.service.UserService;
import com.snwolf.chat.common.user.service.adapter.TextBuilder;
import com.snwolf.chat.common.user.service.adapter.UserAdapter;
import com.snwolf.chat.common.user.service.WXMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Value("${swchat.wx.callback}")
    private String callback;

    private final String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Override
    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMessage) {
        String openId = wxMessage.getFromUser();
        Integer code = getEventKey(wxMessage);
        if(ObjectUtil.isNull(code)){
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = ObjectUtil.isNotNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        if(registered && authorized){
            // todo: 登录成功, 通过code找到channel并且给channel推送消息
        }
        // 没有登录成功
        //  未注册
        if(!registered){
            user = UserAdapter.buildUser(openId);
            userService.register(user);
        }
        // 推送链接让用户授权
        String authUrl = String.format(url, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        log.info("authUrl: {}", authUrl);
        return TextBuilder.build("请点击登录: <a href=\"" + authUrl + "\">登录</a>", wxMessage);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {

    }

    private Integer getEventKey(WxMpXmlMessage wxMessage) {
        try {
            String eventKey = wxMessage.getEventKey();
            String code = eventKey.replaceAll("qrscene_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("get eventKey error, eventKey: {}, e: {}", wxMessage.getEventKey(), e);
            return null;
        }
    }
}
