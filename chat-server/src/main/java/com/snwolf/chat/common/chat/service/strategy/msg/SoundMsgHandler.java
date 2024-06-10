package com.snwolf.chat.common.chat.service.strategy.msg;

import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.entity.msg.SoundMsgDTO;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 语音类型消息处理器
 */
@Component
public class SoundMsgHandler extends AbstractMsgHandler<SoundMsgDTO> {

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.SOUND;
    }

    /**
     * 语音类型消息的消息体是SoundMsgDTO类型, 需要保存在message表的extra字段中
     */
    @Override
    public void saveMsg(Message msg, SoundMsgDTO body) {
        MessageExtra extra = new MessageExtra();
        extra.setSoundMsgDTO(body);
        Message message = new Message();
        message.setId(msg.getId());
        message.setExtra(extra);
        messageDao.updateById(message);
    }

    @Override
    public Object showMsg(Message message) {
        return null;
    }
}
