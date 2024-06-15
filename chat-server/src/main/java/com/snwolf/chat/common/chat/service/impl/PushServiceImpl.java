package com.snwolf.chat.common.chat.service.impl;

import com.snwolf.chat.common.chat.service.PushService;
import com.snwolf.chat.common.chat.service.adapter.PushMessageAdapter;
import com.snwolf.chat.common.common.constant.MQConstant;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Service
public class PushServiceImpl implements PushService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void sendPushMsg(WSBaseResp<?> msg) {
        rocketMQTemplate.convertAndSend(MQConstant.PUSH_MSG_TOPIC, PushMessageAdapter.buildPushMessage(msg));
    }

    @Override
    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
//        uidList.forEach(uid -> rocketMQTemplate.convertAndSend(MQConstant.PUSH_MSG_TOPIC, PushMessageAdapter.buildPushMessage(msg, uid)));
        for (Long uid : uidList) {
            rocketMQTemplate.convertAndSend(MQConstant.PUSH_MSG_TOPIC, PushMessageAdapter.buildPushMessage(msg, uid));
        }
    }
}
