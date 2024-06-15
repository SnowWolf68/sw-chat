package com.snwolf.chat.common.chat.service.strategy.mark;

import com.snwolf.chat.common.chat.domain.enums.MsgMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description:
 */
@Component
public class LikeMsgMarkStrategy extends AbstractMsgMarkStrategy{
    @Override
    protected MsgMarkTypeEnum getMarkType() {
        return MsgMarkTypeEnum.LIKE;
    }


    @Override
    public void mark(Long uid, Long msgId) {
        super.mark(uid, msgId);
        // 如果msgId这条消息之前点过踩, 那么需要取消点踩
        AbstractMsgMarkStrategy disLikeStrategy = MsgMarkStrategyFactory.getStrategyNoNull(MsgMarkTypeEnum.DISLIKE.getCode());
        disLikeStrategy.unMark(uid, msgId);
    }
}
