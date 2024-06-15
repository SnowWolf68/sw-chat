package com.snwolf.chat.common.chat.service.strategy.mark;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.dao.MessageMarkDao;
import com.snwolf.chat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.snwolf.chat.common.chat.domain.entity.MessageMark;
import com.snwolf.chat.common.chat.domain.enums.MsgMarkStatusEnum;
import com.snwolf.chat.common.chat.domain.enums.MsgMarkTypeEnum;
import com.snwolf.chat.common.chat.domain.enums.MsgReqMarkStatusEnum;
import com.snwolf.chat.common.common.event.MessageMarkEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/15/2024
 * @Description: 消息标记抽象实现类, 使用模版设计模式抽象出通用的逻辑
 */
public abstract class AbstractMsgMarkStrategy {

    @Resource
    private MessageDao messageDao;

    @Resource
    private MessageMarkDao messageMarkDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    protected void register(){
        MsgMarkStrategyFactory.register(getMarkType().getCode(), this);
    }


    /**
     * 需子类重写: 得到子类具体的标记类型(点赞/点踩)
     * @return
     */
    protected abstract MsgMarkTypeEnum getMarkType();

    /**
     * 由于子类重写的mark方法中, 有可能存在取消点踩的操作, 这涉及到多张表的更新, 因此这里需要加上事务
     * <br> 下面的unMark方法同理
     * @param uid
     * @param msgId
     */
    @Transactional
    public void mark(Long uid, Long msgId){
        doMark(uid, msgId);
    }

    @Transactional
    public void unMark(Long uid, Long msgId){
        doUnMark(uid, msgId);
    }

    private void doUnMark(Long uid, Long msgId) {
        exec(uid, msgId, getMarkType(), MsgReqMarkStatusEnum.UN_MARK);
    }

    private void doMark(Long uid, Long msgId) {
        exec(uid, msgId, getMarkType(), MsgReqMarkStatusEnum.MARK);
    }

    /**
     * 将标记和取消标记的方法进一步抽象, 抽象成具体的执行方法
     * @param uid
     * @param msgId
     * @param markType
     * @param reqMarkStatus
     */
    protected void exec(Long uid, Long msgId, MsgMarkTypeEnum markType, MsgReqMarkStatusEnum reqMarkStatus){
        MessageMark oldMessageMark = messageMarkDao.getByMsgIdAndMarkType(msgId, markType.getCode());
        if(ObjectUtil.isNull(oldMessageMark) && ObjectUtil.equal(reqMarkStatus.getCode(), MsgReqMarkStatusEnum.UN_MARK.getCode())){
            // 如果要做的是取消标记, 并且之前没有标记过(没有这条记录), 说明这次取消标记无法完成, 直接返回
            return;
        }
        // 更新数据库的信息
        // 数据库中有可能有之前的记录, 也可能没有, 这里需要根据id判断, 如果有, 就更新, 没有, 就新增
        MessageMark updateMessageMark = MessageMark.builder()
                .id(Optional.ofNullable(oldMessageMark).map(MessageMark::getId).orElse(null))
                .msgId(msgId)
                .uid(uid)
                .type(markType.getCode())
                .status(transformStatus(reqMarkStatus).getCode())
                .build();
        boolean result = messageMarkDao.saveOrUpdate(updateMessageMark);
        if(result){
            // 乐观锁判断: 如果新增/更新成功, 那么给前端发送消息通知
            // 发送Spring事件
            ChatMessageMarkDTO chatMessageMarkDTO = ChatMessageMarkDTO.builder()
                    .uid(uid)
                    .msgId(msgId)
                    .actType(reqMarkStatus.getCode())
                    .markType(markType.getCode())
                    .build();
            MessageMarkEvent messageMarkEvent = new MessageMarkEvent(this, chatMessageMarkDTO);
            applicationEventPublisher.publishEvent(messageMarkEvent);
        }
    }

    /**
     * 将前端的消息标记状态枚举类 转成 后端数据库消息标记状态枚举类
     * @param reqMarkStatus
     * @return
     */
    private MsgMarkStatusEnum transformStatus(MsgReqMarkStatusEnum reqMarkStatus) {
        if(ObjectUtil.equal(reqMarkStatus, MsgReqMarkStatusEnum.MARK)){
            return MsgMarkStatusEnum.MARK;
        }else{
            return MsgMarkStatusEnum.UN_MARK;
        }
    }
}
