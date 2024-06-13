package com.snwolf.chat.common;

import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.utils.RedisUtils;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.dto.SummaryInfoDTO;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.service.adapter.SummaryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserDao userDao;

    @Test
    void test1(){
        stringRedisTemplate.opsForValue().set("test", "testvalue");
        String value = stringRedisTemplate.opsForValue().get("test");
        log.info("value: {}", value);
    }

    @Test
    void test2(){
        Boolean result = RedisUtils.set("redisKey", "value");
        String value = RedisUtils.getStr("redisKey");
        log.info("result: {}, value: {}", result, value);
    }

    @Test
    void test3(){
        Boolean result = RedisUtils.set("redisKey", "value");
        String value = RedisUtils.getStr("redisKey");
        log.info("result: {}, value: {}", result, value);
    }

    @Test
    void test4(){
        RLock lock = redissonClient.getLock("123");
        lock.tryLock();
        lock.unlock();
    }

    @Test
    void test5(){
        String token = RedisUtils.get("swchat:userToken:uid_20003", String.class);
        log.info("token: {}", token);
    }

    /**
     * 测试redis的mget在key不存在时, 返回什么数据
     * <p>测试结果: 在mget过程中, 如果有些key不存在, 那么在返回的list中, 对应位置的value为null
     */
    @Test
    void testMget(){
        List<String> result = stringRedisTemplate.opsForValue().multiGet(Arrays.asList("aaa", "bbb"));
        log.info("result: {}", result);
    }

    @Test
    void loadUserSummary(){
        List<Long> ids = Arrays.asList(20001L, 20005L, 20006L, 20007L, 20012L, 20013L, 20031L);
        List<User> userList = userDao.listByIds(ids);
        List<SummaryInfoDTO> userSummaryList = userList.stream()
                .map(SummaryAdapter::buildUserSummary)
                .collect(Collectors.toList());
        RedisUtils.mset(userSummaryList.stream().collect(Collectors.toMap(
                userSummary -> RedisKeyConstant.getKey(RedisKeyConstant.USER_SUMMARY_STRING, userSummary.getUid()),
                userSummary -> userSummary
        )), 60 * 60L);
    }

    /**
     * 测试RedisUtil工具类中的序列化器是什么
     */
    @Test
    void testRedisUtilsSerialize(){
        List<Integer> list = Arrays.asList(20001, 20031);
        RedisUtils.set("testSerialize", list);
    }

}
