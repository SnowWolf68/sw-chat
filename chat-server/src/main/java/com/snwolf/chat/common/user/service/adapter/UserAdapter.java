package com.snwolf.chat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

public class UserAdapter {

    public static User buildUser(String openId) {
        return User.builder()
                .openId(openId)
                .build();
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        return User.builder()
                .id(id)
                .name(userInfo.getNickname())
                .avatar(userInfo.getHeadImgUrl())
                .build();
    }

    public static UserInfoResp buildUserInfo(User user, Integer count) {
        UserInfoResp userInfoResp = BeanUtil.copyProperties(user, UserInfoResp.class);
        userInfoResp.setModifyNameChance(count);
        return userInfoResp;
    }
}
