package com.snwolf.chat.common.chat.dao;

import com.snwolf.chat.common.chat.domain.entity.Contact;
import com.snwolf.chat.common.chat.mapper.ContactMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-06
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    public void refreshOrCreate(Long roomId, List<Long> uidList, LocalDateTime createTime, Long msgId) {
        baseMapper.refreshOrCreate(roomId, uidList, createTime, msgId);
    }
}
