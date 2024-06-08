package com.snwolf.swchat.transaction.service;

import com.snwolf.swchat.transaction.annotation.SecureInvoke;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
public class MQProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMsg(String topic, Object body){
        rocketMQTemplate.convertAndSend(body);
    }

    @SecureInvoke
    public void secureSendMsg(String topic, Object body){
        rocketMQTemplate.convertAndSend(body);
    }
}
