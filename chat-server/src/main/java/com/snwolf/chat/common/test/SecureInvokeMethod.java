package com.snwolf.chat.common.test;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.swchat.transaction.annotation.SecureInvoke;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/7/2024
 * @Description:
 */
@Component
@Slf4j
public class SecureInvokeMethod {

    @SecureInvoke
    public void invoke(Integer code){
        log.info("invoke code:{}", code);
        if(ObjectUtil.equal(code, 1)){
            throw new RuntimeException("code is 1");
        }else{
            log.info("invoke success");
        }
    }
}
