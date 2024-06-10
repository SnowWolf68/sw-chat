package com.snwolf.chat.common.chat.service.impl;

import com.snwolf.chat.common.chat.dao.GroupMemberDao;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.dao.RoomFriendDao;
import com.snwolf.chat.common.chat.domain.entity.GroupMember;
import com.snwolf.chat.common.chat.domain.entity.Room;
import com.snwolf.chat.common.chat.domain.entity.RoomFriend;
import com.snwolf.chat.common.chat.domain.enums.RoomFriendStatusEnum;
import com.snwolf.chat.common.chat.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.chat.service.ChatService;
import com.snwolf.chat.common.chat.service.adapter.MessageAdapter;
import com.snwolf.chat.common.chat.service.cache.RoomCache;
import com.snwolf.chat.common.chat.service.cache.RoomGroupCache;
import com.snwolf.chat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.snwolf.chat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.snwolf.chat.common.common.event.MessageSendEvent;
import com.snwolf.chat.common.common.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Override
    public ChatMessageResp sendMsg(Long uid, ChatMessageReq chatMessageReq) {
        // 判断是否有权限在当前房间中发言
        checkRoom(uid, chatMessageReq.getRoomId());
        // 获取消息对应的handler处理器对象
        AbstractMsgHandler<?> handler = MsgHandlerFactory.getStrategy(chatMessageReq.getMsgType());
        Long msgId = handler.checkAndSave(chatMessageReq, uid);
        // 发布 消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        // 封装ChatMessageResp, 返回
        return MessageAdapter.buildChatMessageResp(uid, chatMessageReq, msgId);
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
