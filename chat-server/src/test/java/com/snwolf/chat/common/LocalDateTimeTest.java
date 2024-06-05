package com.snwolf.chat.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
@SpringBootTest
@Slf4j
public class LocalDateTimeTest {

    @Test
    void getCurrentTime(){
        log.info(System.currentTimeMillis() + "");
    }
}
