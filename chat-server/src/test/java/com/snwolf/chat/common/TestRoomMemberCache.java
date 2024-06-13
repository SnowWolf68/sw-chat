package com.snwolf.chat.common;

import com.snwolf.chat.common.chat.service.cache.RoomMemberCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description:
 */
@SpringBootTest
@Slf4j
public class TestRoomMemberCache {

    @Resource
    private RoomMemberCache roomMemberCache;

    @Test
    void testRoomMemberCache(){
        List<Long> memberIdList = roomMemberCache.getMemberIdList(1L);
        log.info("memberIdList:{}", memberIdList);
    }
}
