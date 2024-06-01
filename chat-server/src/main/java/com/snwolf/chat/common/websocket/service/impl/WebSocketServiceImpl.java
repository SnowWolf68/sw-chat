package com.snwolf.chat.common.websocket.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snwolf.chat.common.common.event.UserOnlineEvent;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.RoleEnum;
import com.snwolf.chat.common.user.service.LoginService;
import com.snwolf.chat.common.user.service.RoleService;
import com.snwolf.chat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import com.snwolf.chat.common.websocket.service.NettyUtils;
import com.snwolf.chat.common.websocket.service.WebSocketService;
import com.snwolf.chat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Resource
    @Lazy
    private WxMpService wxMpService;

    @Resource
    private UserDao userDao;

    @Resource
    private LoginService loginService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoleService roleService;

    /**
     * 管理所有在线用户的channel(登录态/游客态)
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    public static final Duration DURATION = Duration.ofHours(1);
    public static final int MAXIMUM_SIZE = 10000;

    /**
     * 临时保存登录code和channe的映射关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @Override
    public void handleLoginReq(Channel channel) throws WxErrorException {
        // 生成随机code
        Integer code = generateLoginCode(channel);
        // 微信申请带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        // 二维码推送给前端
        sendMsg(channel, WebSocketAdapter.build(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        // todo: 用户下线广播

    }

    @Override
    public void scanLoginSuccess(Integer code, Long id) {
        // 确认连接channel在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(ObjectUtil.isNull(channel)){
            return;
        }
        User user = userDao.getById(id);
        // 移除code
        WAIT_LOGIN_MAP.invalidate(code);
        // 调用登录模块, 获取token
        String token = loginService.login(id);
        loginSuccess(channel, token, user);
    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(ObjectUtil.isNull(channel)){
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if(ObjectUtil.isNull(validUid)){
            // token过期了, 需要发一条消息, 通知前端重新登录
            sendMsg(channel, WebSocketAdapter.buildInvalidResp());
            return;
        }
        User user = userDao.getById(validUid);
        loginSuccess(channel, token, user);
    }

    private void sendMsg(Channel channel, WSBaseResp<?> response) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(response)));
    }

    private void loginSuccess(Channel channel, String token, User user) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO(user.getId()));
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.ADMIN)));
        // 发布用户上线成功的事件
        user.setLastOptTime(LocalDateTime.now());
        user.refreshIp(NettyUtils.getAttr(channel, NettyUtils.IP_KEY));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }

    /**
     * 生成随机码并且存放到map中
     *  注意要求生成的code不能和map中已有的code重复
     * @param channel
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (ObjectUtil.isNotNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
