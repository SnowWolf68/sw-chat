package com.snwolf.chat.common.common.event;

import com.snwolf.chat.common.chat.domain.dto.ChatMessageMarkDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description:
 */
@Getter
public class MessageMarkEvent extends ApplicationEvent {

    private final ChatMessageMarkDTO chatMessageMarkDTO;

    public MessageMarkEvent(Object source, ChatMessageMarkDTO chatMessageMarkDTO) {
        super(source);
        this.chatMessageMarkDTO = chatMessageMarkDTO;
    }
}
