package com.snwolf.chat.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@SpringBootTest
@Slf4j
public class RocketMQTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void testProducer(){
        rocketMQTemplate.convertAndSend("test-topic", "hello world");
    }
}
