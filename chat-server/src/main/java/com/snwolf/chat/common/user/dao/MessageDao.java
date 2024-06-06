package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.Message;
import com.snwolf.chat.common.user.mapper.MessageMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
