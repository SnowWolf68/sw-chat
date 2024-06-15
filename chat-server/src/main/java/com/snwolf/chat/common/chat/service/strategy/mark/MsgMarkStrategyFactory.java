package com.snwolf.chat.common.chat.service.strategy.mark;

import com.snwolf.chat.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description:
 */
public class MsgMarkStrategyFactory {

    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer msgMarkType, AbstractMsgMarkStrategy strategy){
        STRATEGY_MAP.put(msgMarkType, strategy);
    }

    /**
     * 根据消息标记类型获取对应的消息标记策略, 如果不存在则抛出异常
     * @param msgMarkType
     * @return
     */
    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer msgMarkType){
        AbstractMsgMarkStrategy msgMarkStrategy = STRATEGY_MAP.get(msgMarkType);
        AssertUtil.isNotEmpty(msgMarkStrategy, "msgMarkType is not exist");
        return msgMarkStrategy;
    }
}
