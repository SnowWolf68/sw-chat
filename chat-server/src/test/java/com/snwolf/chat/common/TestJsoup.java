package com.snwolf.chat.common;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description:
 */
@Slf4j
public class TestJsoup {

    @Test
    void testConnect() throws IOException {
        Connection connect = Jsoup.connect("http://www.baidu.com");
        connect.timeout(1000);
        Document document = connect.get();
        log.info("document: {}", document);
    }
}
