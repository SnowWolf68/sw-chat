package com.snwolf.chat.common;

import cn.hutool.json.JSONUtil;
import com.snwolf.swchat.transaction.utils.JsonUtils;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/9/2024
 * @Description:
 */
public class JSONUtilsTest {

    @Test
    void test(){
//        System.out.println(JSONUtil.toBean("{}", Integer.class));
        System.out.println(JsonUtils.toObj("{}", Integer.class));

    }
}
