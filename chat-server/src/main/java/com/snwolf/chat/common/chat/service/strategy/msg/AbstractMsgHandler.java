package com.snwolf.chat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.chat.domain.entity.Message;
import com.snwolf.chat.common.chat.domain.enums.MessageTypeEnum;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.common.utils.AssertUtil;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 * @param <T> : 消息体的类型
 */
public abstract class AbstractMsgHandler<T> {

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
     * 校验子类消息类型中使用注解添加的校验规则, 例如@NotNull
     * @param chatMessageReq: 前端传过来的消息对象, chatMessageReq.getBody()得到的就是要校验的消息体
     */
    public void check(ChatMessageReq chatMessageReq){
        T msgBody = (T) BeanUtil.toBean(chatMessageReq.getBody(), messageClass);
        AssertUtil.allCheckValidateThrow(msgBody);
    }


    /**
     * 子类选择性重写
     * <p>子类添加额外的校验规则
     */
    public void checkExtra(){}

    /**
     * 需子类重写
     * <p>在消息保存的时候, 采取两段式保存, 先保存消息基础信息到message表中, 然后再保存不同类型的消息体到消息表中
     * @param msg: 消息基础信息, 之前调用save方法保存到数据库中的对象(这个对象中只是消息的基础信息, 没有消息体的信息)
     * @param request: 这里我们主要关注的是request中的消息体body, 我们需要将这个body保存到消息表中
     *               其中, 这个body包括content和extra两类信息, 在saveMsg方法中, content和extra两类信息我们都需要保存
     */
    public abstract void saveMsg(Message msg, ChatMessageReq request);
}
