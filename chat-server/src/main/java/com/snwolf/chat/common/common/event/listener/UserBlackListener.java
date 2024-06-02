package com.snwolf.chat.common.common.event.listener;

import com.snwolf.chat.common.common.event.UserBlackEvent;
import com.snwolf.chat.common.common.event.UserOnlineEvent;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.UserActiveStatusEnum;
import com.snwolf.chat.common.user.service.IpService;
import com.snwolf.chat.common.websocket.service.WebSocketService;
import com.snwolf.chat.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserBlackListener {

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserDao userDao;


    @Async
    @TransactionalEventListener(value = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent event) throws Throwable {
        User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    @Async
    @TransactionalEventListener(value = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStatus(UserBlackEvent event) throws Throwable {
        userDao.blackUserByUid(event.getUser().getId());
    }
}