package com.snwolf.chat.common.chat.mapper;

import com.snwolf.chat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 会话列表 Mapper 接口
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-06
 */
public interface ContactMapper extends BaseMapper<Contact> {

    void refreshOrCreate(Long roomId, List<Long> uidList, LocalDateTime createTime, Long msgId);
}
