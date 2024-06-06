package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.EmojisMsgDTO;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.entity.msg.VideoMsgDTO;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 表情类型消息处理器
 */
@Component
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.EMOJI;
    }

    /**
     * 语音类型消息的消息体是SoundMsgDTO类型, 需要保存在message表的extra字段中
     */
    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {
        EmojisMsgDTO emojisBody = BeanUtil.toBean(request.getBody(), EmojisMsgDTO.class);
        MessageExtra extra = new MessageExtra();
        extra.setEmojisMsgDTO(emojisBody);
        Message message = new Message();
        message.setId(msg.getId());
        message.setExtra(extra);
        messageDao.updateById(message);
    }
}
