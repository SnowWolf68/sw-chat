package com.snwolf.chat.common.chat.controller;

import com.snwolf.chat.common.chat.domain.entity.ChatMessagePageReq;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageBaseReq;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageMarkReq;
import com.snwolf.chat.common.chat.domain.vo.req.ChatMessageReq;
import com.snwolf.chat.common.chat.domain.vo.resp.ChatMessageResp;
import com.snwolf.chat.common.chat.service.ChatService;
import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/msg")
    @ApiOperation("发送消息")
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq chatMessageReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        Long msgId = chatService.sendMsg(uid, chatMessageReq);
        return ApiResult.success(chatService.buildSingleChatMessageResp(msgId, uid));
    }

    @GetMapping("/public/msg/page")
    @ApiOperation("消息列表")
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq request){
        return ApiResult.success(chatService.getCursorPage(request, RequestHolder.getUserInfo().getUid()));
    }

    @PutMapping("/msg/recall")
    @ApiOperation("撤回消息")
    public ApiResult<Void> recallMsg(@Valid @RequestBody ChatMessageBaseReq chatMessageBaseReq){
        chatService.recallMsg(RequestHolder.getUserInfo().getUid(), chatMessageBaseReq);
        return ApiResult.success();
    }

    @PutMapping("/msg/mark")
    @ApiOperation("消息标记")
    public ApiResult<Void> msgMark(@Valid @RequestBody ChatMessageMarkReq chatMessageMarkReq){
        chatService.msgMark(RequestHolder.getUserInfo().getUid(), chatMessageMarkReq);
        return ApiResult.success();
    }
}
