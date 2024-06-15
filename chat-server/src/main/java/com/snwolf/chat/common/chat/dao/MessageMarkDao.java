package com.snwolf.chat.common.chat.dao;

import com.snwolf.chat.common.chat.domain.entity.MessageMark;
import com.snwolf.chat.common.chat.domain.enums.MsgMarkStatusEnum;
import com.snwolf.chat.common.chat.mapper.MessageMarkMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-15
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public MessageMark getByMsgIdAndMarkType(Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, markType)
                .one();
    }

    public Integer getCountByMsgIdAndMarkType(Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, markType)
                .eq(MessageMark::getStatus, MsgMarkStatusEnum.MARK.getCode())
                .count();
    }
}
