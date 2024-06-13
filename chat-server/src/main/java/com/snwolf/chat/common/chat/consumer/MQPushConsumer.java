package com.snwolf.chat.common.chat.consumer;

import com.snwolf.chat.common.chat.domain.dto.PushMessageDTO;
import com.snwolf.chat.common.chat.domain.enums.WSPushTypeEnum;
import com.snwolf.chat.common.common.constant.MQConstant;
import com.snwolf.chat.common.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Component
@RocketMQMessageListener(topic = MQConstant.PUSH_MSG_TOPIC, consumerGroup = MQConstant.PUSH_MSG_GROUP)
public class MQPushConsumer implements RocketMQListener<PushMessageDTO> {

    @Resource
    private WebSocketService webSocketService;

    /**
     * 使用WebSocketService推送消息
     * @param pushMessageDTO
     */
    @Override
    public void onMessage(PushMessageDTO pushMessageDTO) {
        Integer pushType = pushMessageDTO.getPushType();
        if(Objects.equals(pushType, WSPushTypeEnum.ALL.getType())){
            webSocketService.pushMsgToAll(pushMessageDTO.getWsBaseMsg());
        }else{
            webSocketService.pushMsgToTargetUid(pushMessageDTO.getWsBaseMsg(), pushMessageDTO.getUid());
        }
    }
}
