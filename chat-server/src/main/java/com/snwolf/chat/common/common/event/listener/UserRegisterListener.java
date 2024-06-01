package com.snwolf.chat.common.common.event.listener;

import com.snwolf.chat.common.common.event.UserRegisterEvent;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;
import com.snwolf.chat.common.user.domain.enums.ItemEnum;
import com.snwolf.chat.common.user.service.impl.UserBackpackServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserRegisterListener {

    @Resource
    private UserBackpackServiceImpl userBackpackService;

    @Resource
    private UserDao userDao;

    /**
     * 发送改名卡
     * @param event
     * @throws Throwable
     */
    @Async
    @TransactionalEventListener(value = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event) throws Throwable {
        User user = event.getUser();
        userBackpackService.distributeItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(),
                IdempotentEnum.UID, user.getId().toString());
    }

    /**
     * 发送注册徽章
     * @param event
     * @throws Throwable
     */
    @Async
    @TransactionalEventListener(value = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) throws Throwable {
        int registeredCount = userDao.count();
        User user = event.getUser();
        if(registeredCount < 10){
            userBackpackService.distributeItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(),
                    IdempotentEnum.UID, user.getId().toString());
        }else if(registeredCount < 100){
            userBackpackService.distributeItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(),
                    IdempotentEnum.UID, user.getId().toString());
        }
    }
}