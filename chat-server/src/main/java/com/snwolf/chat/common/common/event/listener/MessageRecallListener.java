package com.snwolf.chat.common.common.event.listener;

import com.snwolf.chat.common.chat.dao.RoomFriendDao;
import com.snwolf.chat.common.chat.domain.entity.Room;
import com.snwolf.chat.common.chat.domain.entity.RoomFriend;
import com.snwolf.chat.common.chat.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.chat.service.ChatService;
import com.snwolf.chat.common.chat.service.PushService;
import com.snwolf.chat.common.chat.service.cache.RoomCache;
import com.snwolf.chat.common.chat.service.cache.RoomMemberCache;
import com.snwolf.chat.common.common.event.MessageRecallEvent;
import com.snwolf.chat.common.websocket.domain.enums.WSRespTypeEnum;
import com.snwolf.chat.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/10/2024
 * @Description:
 */
@Component
public class MessageRecallListener {

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomMemberCache roomMemberCache;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private PushService pushService;

    @Resource
    private ChatService chatService;


    @Async
    @EventListener(MessageRecallEvent.class)
    public void sendMsg(MessageRecallEvent event){
        // 找到房间中的所有用户
        Room room = roomCache.get(event.getChatMsgRecallDTO().getRoomId());
        Long roomId = room.getId();
        List<Long> uidList = null;
        if(room.getType().equals(RoomTypeEnum.GROUP.getType())){
            // 群聊
            uidList = roomMemberCache.getMemberIdList(roomId);
            // 从redis中取出来的对象有可能是Integer类型, 因此这里需要做一次兼容
            for (int i = 0; i < uidList.size(); i++) {
                uidList.set(i, Long.parseLong(String.valueOf(uidList.get(i))));
            }
        }else{
            // 单聊
            RoomFriend roomFriend = roomFriendDao.getByRoomId(roomId);
            uidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
        }
        // 向这些用户的ws推送撤回消息的信息
        ChatMessageResp chatMessageResp = chatService.buildSingleChatMessageResp(event.getChatMsgRecallDTO().getMsgId(), null);
        pushService.sendPushMsg(WebSocketAdapter.buildChatMessageResp(chatMessageResp, WSRespTypeEnum.RECALL), uidList);
    }
}
