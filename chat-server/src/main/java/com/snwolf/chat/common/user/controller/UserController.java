package com.snwolf.chat.common.user.controller;


import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-27
 */
@RestController
@RequestMapping("/capi/user")
public class UserController {

    @GetMapping("/userInfo")
    public UserInfoResp getUserInfo(@RequestParam Long uid) {
        return null;
    }
}

