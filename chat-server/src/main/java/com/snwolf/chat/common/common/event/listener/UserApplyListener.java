package com.snwolf.chat.common.common.event.listener;

import com.snwolf.chat.common.common.event.UserApplyEvent;
import com.snwolf.chat.common.user.dao.UserApplyDao;
import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.websocket.service.WebSocketService;
import com.snwolf.chat.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/4/2024
 * @Description:
 */
@Component
public class UserApplyListener {

    @Resource
    private UserApplyDao userApplyDao;

    @Resource
    private WebSocketService webSocketService;

    /**
     * 给被申请的好友推动一条信息
     *  信息包括: 申请人的id, 被申请人一共有多少未读申请请求
     * @param event
     */
    @Async
    @TransactionalEventListener(value = UserApplyEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event){
        UserApply userApply = event.getUserApply();
        // 获取被申请人一共有多少条未读申请
        Integer count = userApplyDao.getUnReadCount(userApply.getTargetId());
        // 封装响应, ws发送响应
        webSocketService.pushMsgToTargetUid(WebSocketAdapter.buildUserApplyResp(userApply.getUid(), count), userApply.getTargetId());
    }
}
