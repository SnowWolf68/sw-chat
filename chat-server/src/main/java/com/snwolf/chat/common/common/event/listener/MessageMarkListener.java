package com.snwolf.chat.common.common.event.listener;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.chat.dao.MessageMarkDao;
import com.snwolf.chat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.enums.MsgMarkTypeEnum;
import com.snwolf.chat.common.chat.service.PushService;
import com.snwolf.chat.common.chat.service.cache.MessageCache;
import com.snwolf.chat.common.chat.service.cache.RoomMemberCache;
import com.snwolf.chat.common.common.event.MessageMarkEvent;
import com.snwolf.chat.common.user.domain.enums.IdempotentEnum;
import com.snwolf.chat.common.user.domain.enums.ItemEnum;
import com.snwolf.chat.common.user.service.IUserBackpackService;
import com.snwolf.chat.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description:
 */
@Component
public class MessageMarkListener {

    @Resource
    private MessageMarkDao messageMarkDao;

    @Resource
    private MessageCache messageCache;

    @Resource
    private RoomMemberCache roomMemberCache;

    @Resource
    private PushService pushService;

    @Resource
    private IUserBackpackService userBackpackService;


    /**
     * 给房间中的其余用户发送消息标记信息, 让其余用户能够及时更新最新的消息标记人数
     * @param event
     */
    @Async
    @TransactionalEventListener(value = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event){
        ChatMessageMarkDTO chatMessageMarkDTO = event.getChatMessageMarkDTO();
        // 根据msgId以及标记类型markType, 查询房间内使用markType标记msgId这条消息的人数
        Integer count = messageMarkDao.getCountByMsgIdAndMarkType(chatMessageMarkDTO.getMsgId(), chatMessageMarkDTO.getMarkType());
        Message message = messageCache.getMessage(chatMessageMarkDTO.getMsgId());
        List<Long> uidList = roomMemberCache.getMemberIdList(message.getRoomId());
        // 从redis中取出来的对象有可能是Integer类型, 因此这里需要做一次兼容
        for (int i = 0; i < uidList.size(); i++) {
            uidList.set(i, Long.parseLong(String.valueOf(uidList.get(i))));
        }
        pushService.sendPushMsg(WebSocketAdapter.buildMsgMarkResp(chatMessageMarkDTO, count), uidList);
    }


    /**
     * 如果某个用户的一条消息的点赞人数超过10人, 给这个用户分发一个徽章
     * @param event
     */
    @Async
    @TransactionalEventListener(value = MessageMarkEvent.class, fallbackExecution = true)
    public void distributeItem(MessageMarkEvent event) throws Throwable {
        ChatMessageMarkDTO chatMessageMarkDTO = event.getChatMessageMarkDTO();
        // 判断是否是文本消息, 只有文本消息才能升级
        Message message = messageCache.getMessage(chatMessageMarkDTO.getMsgId());
        if(ObjectUtil.notEqual(message.getType(), MessageTypeEnum.TEXT.getType())){
            // 如果不是文本类型的消息, 直接返回
            return;
        }
        if(ObjectUtil.equal(chatMessageMarkDTO.getMarkType(), MsgMarkTypeEnum.LIKE.getCode())){
            // 根据msgId以及标记类型markType, 查询房间内使用markType标记msgId这条消息的人数
            Integer count = messageMarkDao.getCountByMsgIdAndMarkType(chatMessageMarkDTO.getMsgId(), chatMessageMarkDTO.getMarkType());
            if(count > MsgMarkTypeEnum.LIKE.getCount()){
                // 给用户发一个徽章
                // 由于物品发放接口中, 我们已经做过了幂等设计, 因此不需要担心重复发放问题
                // 这里幂等的条件: 一条消息只能发放一次徽章, 因此幂等的类型为msgId, 幂等的值就是具体的msgId
                userBackpackService.distributeItem(chatMessageMarkDTO.getUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MESSAGE_ID, chatMessageMarkDTO.getMsgId().toString());
            }
        }
    }
}
