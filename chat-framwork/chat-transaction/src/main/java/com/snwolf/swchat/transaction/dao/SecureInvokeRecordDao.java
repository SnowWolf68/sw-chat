package com.snwolf.swchat.transaction.dao;

import com.snwolf.swchat.transaction.domain.SecureInvokeStatusEnum;
import com.snwolf.swchat.transaction.domain.entity.SecureInvokeRecord;
import com.snwolf.swchat.transaction.mapper.SecureInvokeRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 本地消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-06
 */
@Service
public class SecureInvokeRecordDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> {

    /**
     * 跳过入库时间在多少分钟之内的记录, 避免刚入库的记录正在被执行的时候触发重试
     */
    public static final long INTERVAL_MINUTES = 2L;

    /**
     * 由于一条记录在入库的同时, 会首先异步执行一次, 所以这里我们应该避免将刚入库的记录再取出来重试
     * <p>我们规定: 跳过入库时间在2分钟之内的记录
     * @return: 所有需要重试的记录
     */
    public List<SecureInvokeRecord> getWaitRetryRecords() {
        return lambdaQuery()
                .eq(SecureInvokeRecord::getStatus, SecureInvokeStatusEnum.WAIT)
                .lt(SecureInvokeRecord::getNextRetryTime, LocalDateTime.now())
                .lt(SecureInvokeRecord::getCreateTime, LocalDateTime.now().minusMinutes(INTERVAL_MINUTES))
                .list();
    }
}
