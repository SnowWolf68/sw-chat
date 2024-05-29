package com.snwolf.chat.common.websocket.service;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class MyAcquireHeaderHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if(o instanceof HttpObject){
            HttpObject httpObject = (HttpObject) o;
            if(httpObject instanceof HttpRequest){
                // todo: 使用Optional改写
                HttpRequest request = (HttpRequest) httpObject;
                UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
                CharSequence charSequence = urlBuilder.getQuery().get("token");
                if(charSequence != null){
                    String token = charSequence.toString();
                    NettyUtils.setAttr(channelHandlerContext.channel(), NettyUtils.TOKEN_KEY, token);
                    request.setUri(urlBuilder.getPath().toString());
                }
            }
        }
        channelHandlerContext.fireChannelRead(o);
    }
}
