package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.entity.msg.ImgMsgDTO;
import com.snwolf.chat.common.chat.domain.entity.msg.MessageExtra;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description: 图片类型消息处理器
 */
@Component
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {

    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.IMG;
    }

    /**
     * 图片类型的消息体body是ImgMsgDTO类型, 需要保存在message表的extra字段中
     */
    @Override
    public void saveMsg(Message msg, ChatMessageReq request) {
        ImgMsgDTO imgBody = BeanUtil.toBean(request.getBody(), ImgMsgDTO.class);
        MessageExtra extra = new MessageExtra();
        extra.setImgMsgDTO(imgBody);
        Message message = new Message();
        message.setId(msg.getId());
        message.setExtra(extra);
        messageDao.updateById(message);
    }
}
