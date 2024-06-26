package com.snwolf.chat.common.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.snwolf.chat.common.websocket.domain.enums.WSReqTypeEnum;
import com.snwolf.chat.common.websocket.domain.vo.request.WSBaseReq;
import com.snwolf.chat.common.websocket.service.NettyUtils;
import com.snwolf.chat.common.websocket.service.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    /**
     * 建立连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        // 保存channel
        webSocketService.connect(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            log.info("握手成功");
            String token = NettyUtils.getAttr(ctx.channel(), NettyUtils.TOKEN_KEY);
            if(StrUtil.isNotBlank(token)){
                webSocketService.authorize(ctx.channel(), token);
            }
        }else if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                log.info("读空闲");
                // todo: 用户下线
                userOffLine(ctx);
            }
        }
    }

    /**
     * 客户端主动下线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        ctx.channel().close();
        webSocketService.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        log.info("接收到消息：{}", text);
        WSBaseReq wsBaseReq = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case AUTHORIZE:
                webSocketService.authorize(channelHandlerContext.channel(), wsBaseReq.getData());
                break;
            case HEARTBEAT:
                break;
            case LOGIN:
                webSocketService.handleLoginReq(channelHandlerContext.channel());
        }
    }
}
