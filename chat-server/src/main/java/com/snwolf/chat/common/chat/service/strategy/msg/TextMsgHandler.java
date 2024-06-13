package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.msg.TextMsgReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
        TextMsgReq textMsgReq = BeanUtil.toBean(body, TextMsgReq.class);
        MessageExtra extra = new MessageExtra();
        Message updateMsg = new Message();
        updateMsg.setId(msg.getId());
        updateMsg.setContent(textMsgReq.getContent());
        if(ObjectUtil.isNotNull(textMsgReq.getReplyMsgId())){
            // 处理回复的消息
            // 计算当前消息和回复的消息之间间隔多少条消息
            int count = messageDao.getGapCount(textMsgReq.getReplyMsgId(), msg.getId(), msg.getRoomId());
            updateMsg.setGapCount(count);
            updateMsg.setReplyMsgId(textMsgReq.getReplyMsgId());
        }
        if(CollectionUtil.isNotEmpty(textMsgReq.getAtUidList())){
            // 处理艾特的成员

        }
        updateMsg.setExtra(extra);
        messageDao.updateById(updateMsg);
    }

    @Override
    public Object showMsg(Message message) {
        // todo: 暂时简单实现一下, 只返回content中的内容
        return message.getContent();
    }
}
