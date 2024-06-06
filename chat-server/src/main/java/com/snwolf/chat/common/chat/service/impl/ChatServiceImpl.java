package com.snwolf.chat.common.chat.service.impl;

import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.service.ChatService;
import com.snwolf.chat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.snwolf.chat.common.chat.service.strategy.msg.MsgHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource
    private MessageDao messageDao;

    @Override
    public void sendMsg(Long uid, ChatMessageReq chatMessageReq) {
        // 判断是否有权限在当前房间中发言
        checkRoom(uid, chatMessageReq.getRoomId());
        // 获取消息对应的handler处理器对象
        AbstractMsgHandler<?> handler = MsgHandlerFactory.getStrategy(chatMessageReq.getMsgType());
        handler.checkAndSave(chatMessageReq, uid);
    }

    /**
     * 检查uid用户是否有权限在roomId这个房间中发言
     * @param uid
     * @param roomId
     */
    private void checkRoom(Long uid, Long roomId) {

    }
}
