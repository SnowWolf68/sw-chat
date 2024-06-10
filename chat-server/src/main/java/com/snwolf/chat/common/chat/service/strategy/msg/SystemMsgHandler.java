package com.snwolf.chat.common.chat.service.strategy.msg;

import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 文本类型消息处理器
 * @note: 系统消息处理器, 系统类型消息不需要封装, String类型的消息直接就是消息体, 因此泛型直接Object
 */
@Component
public class SystemMsgHandler extends AbstractMsgHandler<Object>{

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.SYSTEM;
    }

    /**
     * 系统类型消息, 系统类型消息的消息体body是String类型
     * <p>系统类型消息不携带extra字段, 因此只需要保存content字段即可
     */
    @Override
    public void saveMsg(Message msg, Object body) {
        Message message = new Message();
        message.setId(msg.getId());
        message.setContent((String) body);
        messageDao.updateById(message);
    }

    @Override
    public Object showMsg(Message message) {
        return null;
    }
}
