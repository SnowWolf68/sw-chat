package com.snwolf.chat.common.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/7/2024
 * @Description: 测试本地消息表框架功能
 */
@RestController
@RequestMapping("/testController")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/test")
    public void test(@RequestParam Integer code) {
        testService.testWithTransaction(code);
    }
}
