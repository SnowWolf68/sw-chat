package com.snwolf.chat.common.chat.service;

import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;

import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
public interface PushService {


    /**
     * 将消息推送给mq
     * @param msg
     */
    void sendPushMsg(WSBaseResp<?> msg);

    void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList);
}
