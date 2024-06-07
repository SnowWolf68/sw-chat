package com.snwolf.chat.common.test;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/7/2024
 * @Description:
 */
@Service
@Slf4j
public class TestService {

    @Resource
    private SecureInvokeMethod secureInvokeMethod;

    @Transactional(rollbackFor = Exception.class)
    public void testWithTransaction(Integer code) {
        log.info("testWithTransaction: {}", code);
        secureInvokeMethod.invoke(code);
    }
}
