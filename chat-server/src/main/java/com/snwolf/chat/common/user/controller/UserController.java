package com.snwolf.chat.common.user.controller;

import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import com.snwolf.chat.common.user.domain.vo.req.ModifyNameReq;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import com.snwolf.chat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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

    @GetMapping("/userInfo")
    @ApiOperation("获取用户个人信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo());
    }

    @PutMapping("/name")
    @ApiOperation("用户改名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq modifyNameReq) {
        userService.modifyName(modifyNameReq.getName());
        return ApiResult.success();
    }
}

