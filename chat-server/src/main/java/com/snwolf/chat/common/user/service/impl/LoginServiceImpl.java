package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.snwolf.chat.common.common.constant.RedisKeyConstant;
import com.snwolf.chat.common.common.utils.JwtUtils;
import com.snwolf.chat.common.common.utils.RedisUtils;
import com.snwolf.chat.common.user.service.LoginService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEW_DAYS = 1;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        if(ObjectUtil.isNull(uid)){
            return;
        }
        String userTokenKey = getUserTokenKey(uid);
        Long expireDays = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if(expireDays == -2){
            // 不存在的key
            return;
        }
        if(expireDays < TOKEN_RENEW_DAYS){
            RedisUtils.expire(userTokenKey, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
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
        if (ObjectUtil.isNull(uid)) {
            return null;
        }
        String userTokenKey = getUserTokenKey(uid);
        String oldToken = RedisUtils.get(userTokenKey);
        if (StrUtil.isBlank(oldToken)) {
            return null;
        }
        return Objects.equals(oldToken, token) ? uid : null;
    }

    private static String getUserTokenKey(Long uid) {
        return RedisKeyConstant.getKey(RedisKeyConstant.USER_TOKEN_STRING, uid);
    }
}
