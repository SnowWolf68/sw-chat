package com.snwolf.chat.common.chat.consumer;

import com.snwolf.chat.common.chat.dao.*;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.Room;
import com.snwolf.chat.common.chat.domain.entity.RoomFriend;
import com.snwolf.chat.common.chat.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.chat.service.PushService;
import com.snwolf.chat.common.chat.service.adapter.MessageAdapter;
import com.snwolf.chat.common.chat.service.cache.RoomCache;
import com.snwolf.chat.common.chat.service.cache.RoomGroupCache;
import com.snwolf.chat.common.common.constant.MQConstant;
import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.utils.RedisUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description: 收到发送的消息, 将消息推送给所有在线用户
 */
@Component
@RocketMQMessageListener(topic = MQConstant.SEND_MSG_TOPIC, consumerGroup = MQConstant.SEND_MSG_GROUP)
public class MsgSendConsumer implements RocketMQListener<Long> {

    @Resource
    private MessageDao messageDao;

    @Resource
    private RoomDao roomDao;

    @Resource
    private RoomCache roomCache;

    @Resource
    private PushService pushService;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberDao groupMemberDao;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private ContactDao contactDao;

    @Override
    public void onMessage(Long msgId) {
        // 根据消息id查到消息对象
        Message message = messageDao.getById(msgId);
        // 查到消息对应的房间
        Room room = roomDao.getById(message.getRoomId());
        // 更新room表中的lastMsgId和activeTime
        Long roomId = room.getId();
        roomDao.updateActiveTimeAndLastMsgId(roomId, msgId, message.getCreateTime());
        // 更新redis中存储的activeTime
        RedisUtils.zAdd(RedisKeyConstant.getKey(RedisKeyConstant.ROOM_ACTIVE_TIME_STRING), roomId, message.getCreateTime().toEpochSecond(ZoneOffset.UTC));
        // 清除RoomCache中房间的缓存, 保证数据一致性
        roomCache.delete(roomId);
        // 根据消息类型, 发送消息
        // 如果是全员群的消息, 直接推送给所有在线用户
        if(room.isHotRoom()){
            // 全员群, 推送给所有在线用户
            pushService.sendPushMsg(MessageAdapter.buildWSBaseResp(message));
        }else{
            // 如果是群聊或单聊消息, 首先找到收消息的成员列表, 然后推送 带着收消息成员列表 的消息
            List<Long> uidList = null;
            if(Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())){
                // 群聊消息
                // 获取所有群成员
                Long groupId = roomGroupCache.get(roomId);
                uidList = groupMemberDao.getMemberIdsByGroupId(groupId);
            }else if(Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())){
                // 单聊消息
                RoomFriend roomFriend = roomFriendDao.getByRoomId(roomId);
                uidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            // 更新所有收到消息的人的会话时间, 如果不存在这条记录就插入
            contactDao.refreshOrCreate(roomId, uidList, message.getCreateTime(), message.getId());
            // 推送消息
            pushService.sendPushMsg(MessageAdapter.buildWSBaseResp(message), uidList);
        }
    }
}
