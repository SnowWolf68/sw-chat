package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.utils.JwtUtils;
import com.snwolf.chat.common.common.utils.RedisUtils;
import com.snwolf.chat.common.user.service.LoginService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public void renewalTokenIfNecessary(String token) {

    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if(ObjectUtil.isNull(uid)){
            return null;
        }
        String userTokenKey = getUserTokenKey(uid);
        String oldToken = RedisUtils.get(userTokenKey);
        if(StrUtil.isBlank(oldToken)){
            return null;
        }
        return uid;
    }

    private static String getUserTokenKey(Long uid) {
        return RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN_STRING, uid);
    }
}
