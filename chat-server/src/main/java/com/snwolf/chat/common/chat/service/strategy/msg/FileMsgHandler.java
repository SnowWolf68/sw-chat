package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.FileMsgDTO;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 文件类型消息处理器
 */
@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.FILE;
    }

    /**
     * 文件类型消息的消息体是FIleMsgDTO类型, 需要保存在message表的extra字段中
     */
    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {
        FileMsgDTO fileBody = BeanUtil.toBean(request.getBody(), FileMsgDTO.class);
        MessageExtra extra = new MessageExtra();
        extra.setFileMsg(fileBody);
        Message message = new Message();
        message.setId(msg.getId());
        message.setExtra(extra);
        messageDao.updateById(message);
    }
}
