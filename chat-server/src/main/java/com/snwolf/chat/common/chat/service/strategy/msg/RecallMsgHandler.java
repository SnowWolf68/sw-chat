package com.snwolf.chat.common.chat.service.strategy.msg;

import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.dto.ChatMsgRecallDTO;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.entity.msg.MsgRecall;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.common.event.MessageRecallEvent;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.service.cache.UserCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/10/2024
 * @Description:
 */
@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object> {

    @Resource
    private UserCache userCache;

    @Resource
    private MessageDao messageDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        // 通过uid获取用户信息的业务很常用, 这里用批量缓存框架做一层redis缓存
        User userInfo = userCache.getUserInfo(msg.getFromUid());
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    /**
     * 消息撤回的业务代码
     *
     * @param recallUid
     * @param message
     */
    public void recall(Long recallUid, Message message) {
        MessageExtra extra = message.getExtra();
        extra.setRecall(MsgRecall.builder().recallUid(recallUid).build());
        // 更新消息表中对应消息的信息
        Message updateMessage = Message.builder()
                .id(message.getId())
                .type(MessageTypeEnum.RECALL.getType())
                .extra(extra)
                .build();
        messageDao.updateById(updateMessage);
        // 发送spring消息撤回的事件, 事件的监听者对所有消息的接收者的ws推送撤回信息
        applicationEventPublisher.publishEvent(
                new MessageRecallEvent(this,
                        new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));
    }

    @Override
    public void saveMsg(Message msg, Object body) {
        throw new UnsupportedOperationException();
    }
}
