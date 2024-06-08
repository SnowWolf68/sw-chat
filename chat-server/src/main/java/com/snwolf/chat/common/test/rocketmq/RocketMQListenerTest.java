package com.snwolf.chat.common.test.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Component
@RocketMQMessageListener(topic = "test-topic", consumerGroup = "test-group")
@Slf4j
public class RocketMQListenerTest implements RocketMQListener<String> {


    @Override
    public void onMessage(String message) {
        log.info("receive message: " + message);
    }
}
