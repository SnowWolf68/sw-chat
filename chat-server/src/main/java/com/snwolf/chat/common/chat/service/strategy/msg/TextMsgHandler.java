package com.snwolf.chat.common.chat.service.strategy.msg;

import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 文本类型消息处理器
 * @note: 文本消息的消息体不需要额外的包装类来接收, 因此泛型是Object
 */
@Component
public class TextMsgHandler extends AbstractMsgHandler<Object>{

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.TEXT;
    }

    /**
     * 文本类型的消息体需要保存在content中, 并且文本类型的消息也有可能携带extra, 因此content和extra我们都需要保存
     * @param msg: 消息基础信息, 之前调用save方法保存到数据库中的对象(这个对象中只是消息的基础信息, 没有消息体的信息)
     * @param body: 文本消息的消息体
     */
    @Override
    public void saveMsg(Message msg, Object body) {
        Message msgBody = new Message();
        // 注意msg中可能会携带extra, 但是在第一阶段的保存中, 没有将extra保存到message表中, 因此这里我们需要保存msg中的extra
        // 注意msg.getExtra()有可能为null
        MessageExtra extra = Optional.ofNullable(msg.getExtra())
                .orElse(new MessageExtra());
        msgBody.setId(msg.getId());
        msgBody.setExtra(extra);
        // todo: 文本类型的消息体其实也包括了艾特内容, 回复内容,
        //  后续需要将文本类型的消息也做一层封装, 但是这里为了处理简单, 我们假设文本类型的消息体只是String类型, 所以这里直接强转body为String
        msgBody.setContent((String) body);
        messageDao.updateById(msgBody);
    }

    @Override
    public Object showMsg(Message message) {
        // todo: 暂时简单实现一下, 只返回content中的内容
        return message.getContent();
    }
}
