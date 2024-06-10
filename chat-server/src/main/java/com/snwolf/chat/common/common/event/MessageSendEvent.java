package com.snwolf.chat.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Getter
public class MessageSendEvent extends ApplicationEvent {

    private Long msgId;

    public MessageSendEvent(Object source, Long msgId){
        super(source);
        this.msgId = msgId;
    }
}
