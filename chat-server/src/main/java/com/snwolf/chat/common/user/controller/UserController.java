package com.snwolf.chat.common.user.controller;

import com.snwolf.chat.common.common.domain.vo.req.BlackReq;
import com.snwolf.chat.common.common.domain.vo.req.WearingBadgeReq;
import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.common.utils.RequestHolder;
import com.snwolf.chat.common.user.domain.enums.RoleEnum;
import com.snwolf.chat.common.user.domain.vo.req.ModifyNameReq;
import com.snwolf.chat.common.user.domain.vo.resp.BadgesResp;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import com.snwolf.chat.common.user.service.RoleService;
import com.snwolf.chat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-27
 * @module UserController
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户个人信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo());
    }

    @PutMapping("/name")
    @ApiOperation("用户改名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq modifyNameReq) {
        userService.modifyName(RequestHolder.getUserInfo().getUid(), modifyNameReq.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章列表预览")
    public ApiResult<List<BadgesResp>> badges(){
        return ApiResult.success(userService.badges(RequestHolder.getUserInfo().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<?> wearingBadge(@Valid @RequestBody WearingBadgeReq wearingBadgeReq){
        userService.wearingBagde(RequestHolder.getUserInfo().getUid(), wearingBadgeReq.getItemId());
        return ApiResult.success();
    }

    @PostMapping("/black")
    @ApiOperation("拉黑")
    public ApiResult<?> black(@Valid @RequestBody BlackReq blackReq){
        Long uid = RequestHolder.getUserInfo().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower, "无权限");
        userService.black(blackReq.getId());
        return ApiResult.success();
    }
}

