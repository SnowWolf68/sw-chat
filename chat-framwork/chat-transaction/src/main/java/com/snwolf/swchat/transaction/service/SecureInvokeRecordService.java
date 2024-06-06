package com.snwolf.swchat.transaction.service;

import com.snwolf.swchat.transaction.domain.entity.SecureInvokeRecord;

/**
 * <p>
 * 本地消息表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-06
 */
public interface SecureInvokeRecordService {

    void invoke(SecureInvokeRecord secureInvokeRecord, boolean async);
}
