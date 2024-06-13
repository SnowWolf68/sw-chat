package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.enums.MessageStatusEnum;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.req.msg.TextMsgReq;
import com.snwolf.chat.common.chat.domain.vo.resp.msg.TextMsgResp;
import com.snwolf.chat.common.chat.service.cache.MessageCache;
import com.snwolf.chat.common.common.constant.MessageConstant;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.RoleEnum;
import com.snwolf.chat.common.user.service.RoleService;
import com.snwolf.chat.common.user.service.cache.UserCache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Resource
    private UserCache userCache;

    @Resource
    private RoleService roleService;

    @Resource
    private MessageCache messageCache;

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
            extra.setAtUidList(textMsgReq.getAtUidList());
        }
        updateMsg.setExtra(extra);
        messageDao.updateById(updateMsg);
    }

    @Override
    protected void checkExtra(ChatMessageReq req, Long uid) {
        TextMsgReq textMsgReq = BeanUtil.toBean(req.getBody(), TextMsgReq.class);
        // 校验回复的消息是否合法
        if(ObjectUtil.isNotNull(textMsgReq.getReplyMsgId())){
            Message replyMsg = messageDao.getById(textMsgReq.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复的消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), req.getRoomId(), "只能回复同一房间中的消息");
        }
        // 校验艾特列表是否合法
        List<Long> atUidList = textMsgReq.getAtUidList();
        if(CollectionUtil.isNotEmpty(atUidList)){
            // 判断艾特的成员是否存在
            Map<Long, User> userMap = userCache.getBatch(atUidList);
            AssertUtil.equal(userMap.keySet().size(), atUidList.size(), "有一些艾特的成员不存在");
            // 判断是否有艾特全部成员的权限
            // 这里规定, 如果艾特全部成员, 那么aiUidList中会传入一个0值
            if(atUidList.contains(0L)){
                AssertUtil.isTrue(roleService.hasPower(uid, RoleEnum.CHAT_MANAGER), "只有管理员才能艾特全体成员");
            }
        }
    }

    @Override
    public Object showMsg(Message message) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(message.getContent());
        resp.setAtUidList(Optional.ofNullable(message.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
        // 判断是否有回复消息
        Optional<Message> reply = Optional.ofNullable(message.getReplyMsgId())
                .map(messageCache::getMessage)
                .filter(a -> Objects.equals(a.getStatus(), MessageStatusEnum.NORMAL.getStatus()));
        if (reply.isPresent()) {
            Message replyMessage = reply.get();
            TextMsgResp.ReplyMsg replyMsgVO = new TextMsgResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setUid(replyMessage.getFromUid());
            replyMessage.setType(replyMessage.getType());
            replyMsgVO.setBody(MsgHandlerFactory.getStrategy(replyMessage.getType()).showReplyMsg(replyMessage));
            User replyUser = userCache.getUserInfo(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            replyMsgVO.setCanCallback(message.getGapCount() <= MessageConstant.CAN_CALLBACK_GAP_COUNT ? StatusEnum.STATUS_VALID.getStatus() : StatusEnum.STATUS_INVALID.getStatus());
            replyMsgVO.setGapCount(message.getGapCount());
            resp.setReply(replyMsgVO);
        }
        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }
}
