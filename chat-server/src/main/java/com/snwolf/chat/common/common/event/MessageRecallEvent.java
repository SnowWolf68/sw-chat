package com.snwolf.chat.common.common.event;

import com.snwolf.chat.common.chat.domain.dto.ChatMsgRecallDTO;
import lombok.*;
import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/10/2024
 * @Description:
 */
@Getter
public class MessageRecallEvent extends ApplicationEvent {

    private final ChatMsgRecallDTO chatMsgRecallDTO;

    public MessageRecallEvent(Object source, ChatMsgRecallDTO chatMsgRecallDTO) {
        super(source);
        this.chatMsgRecallDTO = chatMsgRecallDTO;
    }
}
