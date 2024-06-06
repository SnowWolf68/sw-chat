package com.snwolf.chat.common.chat.service.strategy.msg;

import com.snwolf.chat.common.common.exception.CommonErrorEnum;
import com.snwolf.chat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
public class MsgHandlerFactory {

    /**
     * <消息类型, handler>
     */
    private static final Map<Integer, AbstractMsgHandler<?>> STRATEGY_MAP = new HashMap<>();

    /**
     * 注册消息处理器到消息处理器工程
     * @param type: 消息处理器的类型
     * @param handler: 消息处理器的handler对象
     */
    public static void register(Integer type, AbstractMsgHandler<?> handler){
        STRATEGY_MAP.put(type, handler);
    }

    /**
     * 通过消息类型获取对应的处理器handler对象
     * @param type: 消息类型
     * @return: 消息对应的handler处理器对象
     * @note 如果type对应的消息处理器不存在, 那么抛出业务异常
     */
    public static AbstractMsgHandler<?> getStrategy(Integer type){
        AbstractMsgHandler<?> handler = STRATEGY_MAP.get(type);
        AssertUtil.isNotEmpty(handler, CommonErrorEnum.HANDLER_NOT_EXIST);
        return handler;
    }
}
