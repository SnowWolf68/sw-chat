package com.snwolf.chat.common.chat.controller;

import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.service.ChatService;
import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import com.snwolf.chat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 * @module: ChatController
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ChatController {

    @Resource
    private ChatService chatService;

    /**
     * todo: 发送消息的方法需要返回消息对象, 便于当前用户的前端展示, 但是这里暂时不进行实现
     */
    @PostMapping("/msg")
    @ApiOperation("发送消息")
    public ApiResult<Void> sendMsg(@Valid @RequestBody ChatMessageReq chatMessageReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        chatService.sendMsg(uid, chatMessageReq);
        return ApiResult.success();
    }
}
