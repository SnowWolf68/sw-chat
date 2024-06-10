package com.snwolf.chat.common.chat.dao;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.chat.domain.entity.ChatMessagePageReq;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.chat.mapper.MessageMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.common.exception.BusinessException;
import com.snwolf.chat.common.common.utils.CursorUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-06
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    @SneakyThrows
    public CursorPageBaseResp<Message> cursorPageQuery(ChatMessagePageReq request, Long receiveUid, Long lastMsgId) {
        return CursorUtils.cursorPageQuery(request, (wrapper) -> {
            wrapper.eq(Message::getRoomId, request.getRoomId());
            wrapper.lt(ObjectUtil.isNotNull(lastMsgId), Message::getId, lastMsgId);
        }, this, Message::getId);
    }
}
