package com.snwolf.chat.common.user.controller;


import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.req.PageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.common.domain.vo.resp.PageBaseResp;
import com.snwolf.chat.common.common.utils.RequestHolder;
import com.snwolf.chat.common.user.domain.vo.req.FriendApplyReq;
import com.snwolf.chat.common.user.domain.vo.req.FriendApproveReq;
import com.snwolf.chat.common.user.domain.vo.req.FriendCheckReq;
import com.snwolf.chat.common.user.domain.vo.resp.FriendApplyResp;
import com.snwolf.chat.common.user.domain.vo.resp.FriendCheckResp;
import com.snwolf.chat.common.user.domain.vo.resp.FriendUnreadResp;
import com.snwolf.chat.common.user.service.UserFriendService;
import com.snwolf.chat.common.websocket.domain.vo.response.ChatMemberResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 用户联系人表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-04
 * @module: userFriendController
 */
@RestController
@RequestMapping("/capi/user/friend")
@Api("联系人相关接口")
public class UserFriendController {

    @Resource
    private UserFriendService userFriendService;

    @GetMapping("/check")
    @ApiOperation("批量判断是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq friendCheckReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        return ApiResult.success(userFriendService.check(uid, friendCheckReq));
    }

    @ApiOperation("联系人列表")
    @GetMapping("/page")
    public ApiResult<CursorPageBaseResp<ChatMemberResp>> page(@Valid CursorPageBaseReq cursorPageBaseReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        return ApiResult.success(userFriendService.frientList(uid, cursorPageBaseReq));
    }

    @PostMapping("/apply")
    @ApiOperation("申请好友")
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq friendApplyReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        userFriendService.apply(uid, friendApplyReq);
        return ApiResult.success();
    }

    @GetMapping("/apply/unread")
    @ApiOperation("申请未读数")
    public ApiResult<FriendUnreadResp> unreadCount(){
        Long uid = RequestHolder.getUserInfo().getUid();
        return ApiResult.success(userFriendService.unreadCount(uid));
    }

    @GetMapping("/apply/page")
    @ApiOperation("好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request){
        Long uid = RequestHolder.getUserInfo().getUid();
        return ApiResult.success(userFriendService.page(uid, request));
    }

    @PutMapping("/apply")
    @ApiOperation("同意好友申请")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq friendApproveReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        userFriendService.applyApprove(uid, friendApproveReq);
        return ApiResult.success();
    }
}

