package com.snwolf.chat.common.common.event.listener;

import com.snwolf.chat.common.common.constant.MQConstant;
import com.snwolf.chat.common.common.event.MessageSendEvent;
import com.snwolf.swchat.transaction.service.MQProducer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Component
public class MessageSendListener {

    @Resource
    private MQProducer mqProducer;

    @TransactionalEventListener(value = MessageSendEvent.class, phase = TransactionPhase.BEFORE_COMMIT, fallbackExecution = true)
    public void messageRoute(MessageSendEvent messageSendEvent){
        Long msgId = messageSendEvent.getMsgId();
        mqProducer.sendMsg(MQConstant.SEND_MSG_TOPIC, msgId);
    }
}
