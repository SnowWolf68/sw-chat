package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.chat.dao.MessageDao;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.service.adapter.MessageAdapter;
import com.snwolf.chat.common.common.utils.AssertUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 * @param <T> : 消息体的类型
 */
public abstract class AbstractMsgHandler<T> {

    @Resource
    private MessageDao messageDao;

    Class<?> messageClass;

    /**
     * 每一个继承当前抽象handler的子类在构造完成的时候, 都会调用@PostConstruct标记的方法
     * <p>这个方法用于将子类handler注册到消息处理器工厂, 便于后续获取
     */
    @PostConstruct
    private void register(){
        MsgHandlerFactory.register(getMsgType().getType(), this);
    }

    /**
     * 在构造方法中将类上的泛型解析成消息体对应的类型, 保存到成员位置
     */
    protected AbstractMsgHandler() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.messageClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    /**
     * 需子类重写
     * @return: 返回消息类型
     */
    abstract MessageTypeEnum getMsgType();


    /**
     * 子类选择性重写
     * <p>子类添加额外的校验规则
     */
    protected void checkExtra(T body, Long roomId, Long uid){}

    /**
     * 需子类重写
     * <p>在消息保存的时候, 采取两段式保存, 先保存消息基础信息到message表中, 然后再保存不同类型的消息体到消息表中
     * @param msg: 消息基础信息, 之前调用save方法保存到数据库中的对象(这个对象中只是消息的基础信息, 没有消息体的信息)
     * @param body: 消息体对象, 这个对象是已经转换成了子类中具体消息体类型的对象
     */
    public abstract void saveMsg(Message msg, T body);

    /**
     * 校验并保存消息
     *
     * @param chatMessageReq: 前端传过来的消息内容
     * @param uid:            发送人uid
     * @return : 消息id(msgId)
     */
    public Long checkAndSave(ChatMessageReq chatMessageReq, Long uid) {
        // 校验消息实体类上的注解
        T msgBody = toBean(chatMessageReq.getBody());
        AssertUtil.allCheckValidateThrow(msgBody);
        // 子类拓展校验
        checkExtra(msgBody, chatMessageReq.getRoomId(), uid);
        // 统一保存
        Message baseMessage = MessageAdapter.buildMessageWithoutBody(uid, chatMessageReq);
        messageDao.save(baseMessage);
        // 子类保存消息体
        saveMsg(baseMessage, msgBody);
        return baseMessage.getId();
    }

    /**
     * 如果body是String类型, 那么使用BeanUtil.toBean()方法无法正确得到String类型的对象, 因此这里需要重写toBean方法
     * <p>用来兼容body为String类型的情况
     * @param body
     * @return
     */
    private T toBean(Object body){
        if(messageClass.isAssignableFrom(body.getClass())){
            return (T) body;
        }
        return (T) BeanUtil.toBean(body, messageClass);
    }
}
