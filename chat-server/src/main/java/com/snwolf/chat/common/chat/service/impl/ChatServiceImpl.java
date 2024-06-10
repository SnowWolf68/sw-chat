package com.snwolf.chat.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.snwolf.chat.common.chat.dao.*;
import com.snwolf.chat.common.chat.domain.entity.*;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.enums.RoomFriendStatusEnum;
import com.snwolf.chat.common.chat.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageBaseReq;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.chat.service.ChatService;
import com.snwolf.chat.common.chat.service.adapter.MessageAdapter;
import com.snwolf.chat.common.chat.service.cache.RoomCache;
import com.snwolf.chat.common.chat.service.cache.RoomGroupCache;
import com.snwolf.chat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.snwolf.chat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.snwolf.chat.common.chat.service.strategy.msg.RecallMsgHandler;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.common.event.MessageSendEvent;
import com.snwolf.chat.common.common.exception.BusinessException;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.user.domain.enums.BlackTypeEnum;
import com.snwolf.chat.common.user.domain.enums.RoleEnum;
import com.snwolf.chat.common.user.service.RoleService;
import com.snwolf.chat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource
    private MessageDao messageDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberDao groupMemberDao;

    @Resource
    private ContactDao contactDao;

    @Resource
    private RoomDao roomDao;

    @Resource
    private UserCache userCache;

    @Resource
    private RoleService roleService;

    @Resource
    private RecallMsgHandler recallMsgHandler;

    @Override
    public Long sendMsg(Long uid, ChatMessageReq chatMessageReq) {
        // 判断是否有权限在当前房间中发言
        checkRoom(uid, chatMessageReq.getRoomId());
        // 获取消息对应的handler处理器对象
        AbstractMsgHandler<?> handler = MsgHandlerFactory.getStrategy(chatMessageReq.getMsgType());
        Long msgId = handler.checkAndSave(chatMessageReq, uid);
        // 发布 消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        // 封装ChatMessageResp, 返回
        return msgId;
    }

    @Override
    public ChatMessageResp buildSingleChatMessageResp(Long msgId, Long receiveUid) {
        return CollectionUtil.getFirst(buildChatMessageRespBatchWithMsgIds(Collections.singletonList(msgId), receiveUid));
    }

    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        Message message = messageDao.getById(request.getMsgId());
        // 判断是否有撤回权限
        checkRecallPermission(uid, message);
        // 撤回消息(使用RecallMsgHandler中的内聚方法)
        recallMsgHandler.recall(uid, message);
    }

    /**
     * 判断是否有消息撤回的权限, 只要是群聊管理员(普通管理员), 即可拥有撤回他人信息的权限
     * @param uid
     * @param message
     */
    private void checkRecallPermission(Long uid, Message message) {
        boolean hasPower = roleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if(!(hasPower || uid.equals(message.getFromUid()))){
            throw new BusinessException("没有撤回权限");
        }
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL, "消息已被撤回");
        AssertUtil.isTrue(LocalDateTime.now().minusMinutes(2L).compareTo(message.getCreateTime()) < 0, "超过2分钟的消息无法撤回");
        recallMsgHandler.recall(uid, message);
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getCursorPage(ChatMessagePageReq request, Long receiveUid) {
        // 获取当前用户能够看到的当前房间的最后一条消息id(如果是全员群, 则不做判断), 适用于当前receiveUid被移除群聊之后的场景
        // lastMsgId有可能为null
        Long lastMsgId = getLastMsgId(request.getRoomId(), receiveUid);
        // 游标翻页查询
        CursorPageBaseResp<Message> cursorPageResult = messageDao.cursorPageQuery(request, receiveUid, lastMsgId);
        // 过滤掉被拉黑用户的消息
        Set<String> blackSet = userCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType(), new HashSet<>());
        cursorPageResult.getList().removeIf(message -> blackSet.contains(message.getFromUid().toString()));
        // 将Message对象转换成前端需要的ChatMessageResp对象
        List<ChatMessageResp> chatMessageRespList = buildChatMessageRespBatch(cursorPageResult.getList(), receiveUid);
        // 返回
        return CursorPageBaseResp.init(cursorPageResult, chatMessageRespList);
    }

    private List<ChatMessageResp> buildChatMessageRespBatchWithMsgIds(List<Long> messageIdList, Long receiveUid){
        List<Message> messageList = messageDao.listByIds(messageIdList);
        return buildChatMessageRespBatch(messageList, receiveUid);
    }

    /**
     * 批量的将Message转换成ChatMessageResp
     * @param messageList
     * @param receiveUid
     * @return
     */
    private List<ChatMessageResp> buildChatMessageRespBatch(List<Message> messageList, Long receiveUid){
        return messageList.stream()
                .map(message -> {
                    ChatMessageResp.MessageMark messageMark = getMessageMark(message, receiveUid);
                    ChatMessageResp.Message chatMessage = getMessage(message, messageMark);
                    ChatMessageResp.UserInfo userInfo = getUserInfo(message);
                    return ChatMessageResp.builder()
                            .message(chatMessage)
                            .fromUser(userInfo)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Long getLastMsgId(Long roomId, Long receiveUid) {
        // 如果是全员群, 则不做判断
        Room room = roomDao.getById(roomId);
        if(room.isHotRoom()){
            return null;
        }
        return contactDao.getLastMsgId(roomId, receiveUid);
    }

    private ChatMessageResp.UserInfo getUserInfo(Message message) {
        return ChatMessageResp.UserInfo
                .builder()
                .uid(message.getFromUid())
                .build();
    }

    private ChatMessageResp.Message getMessage(Message message, ChatMessageResp.MessageMark messageMark) {
        AbstractMsgHandler<?> handler = MsgHandlerFactory.getStrategy(message.getType());
        return ChatMessageResp.Message.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .sendTime(message.getCreateTime())
                .type(message.getType())
                .body(handler.showMsg(message))
                .messageMark(messageMark)
                .build();
    }

    private ChatMessageResp.MessageMark getMessageMark(Message message, Long receiveUid) {
        return ChatMessageResp.MessageMark
                .builder()
                .likeCount(0)
                .userLike(StatusEnum.STATUS_INVALID.getStatus())
                .dislikeCount(0)
                .userDislike(StatusEnum.STATUS_INVALID.getStatus())
                .build();
    }

    /**
     * 检查uid用户是否有权限在roomId这个房间中发言
     * @param uid
     * @param roomId
     */
    private void checkRoom(Long uid, Long roomId) {
        Room room = roomCache.get(roomId);
        // 如果room是全员群, 那么所有用户都可以在其中发消息, 不用校验权限
        if(room.isHotRoom()){
            return;
        }
        // 如果是单聊, 判断当前roomFriend的状态是否正常(当前两人都未删除对方好友)
        if(Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())){
            RoomFriend roomFriend = roomFriendDao.getByRoomId(roomId);
            AssertUtil.equal(roomFriend.getStatus(), RoomFriendStatusEnum.NO.getCode(), "您已经不是对方的好友");
        }
        // 如果是群聊, 判断当前用户是不是已经不在群聊中
        if(Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())){
            // 根据roomId查询出groupId, 由于群聊数量有限, 并且通过roomId查询groupId的操作经常被用到, 所以这里做一层缓存
            Long groupId = roomGroupCache.get(roomId);
            // 根据groupId到group_member表中查询当前用户是不是在当前group中
            GroupMember groupMember = groupMemberDao.getByGroupIdAndUid(groupId, uid);
            AssertUtil.isNotEmpty(groupMember, "您已经被移除该群聊");
        }
    }
}
