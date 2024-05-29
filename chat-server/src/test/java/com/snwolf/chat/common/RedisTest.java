package com.snwolf.chat.common;

import com.snwolf.chat.common.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test1(){
        stringRedisTemplate.opsForValue().set("test", "testvalue");
        String value = stringRedisTemplate.opsForValue().get("test");
        log.info("value: {}", value);
    }

    @Test
    void test2(){
        Boolean result = RedisUtils.set("redisKey", "value");
        String value = RedisUtils.get("redisKey");
        log.info("result: {}, value: {}", result, value);
    }

    @Test
    void test3(){
        Boolean result = RedisUtils.set("redisKey", "value");
        String value = RedisUtils.get("redisKey");
        log.info("result: {}, value: {}", result, value);
    }

    @Test
    void test4(){
        RLock lock = redissonClient.getLock("123");
        lock.tryLock();
        lock.unlock();
    }

}
