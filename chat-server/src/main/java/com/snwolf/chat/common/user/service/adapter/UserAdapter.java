package com.snwolf.chat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.user.domain.entity.ItemConfig;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.domain.vo.resp.BadgesResp;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.*;
import java.util.stream.Collectors;

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

    public static List<BadgesResp> buildBadgesResp(List<ItemConfig> itemConfigs, List<UserBackpack> userBackpacks, User user) {
        Set<Long> userBackpacksItemIds = userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream()
                .map(itemConfig -> {
                    BadgesResp badgesResp = BeanUtil.copyProperties(itemConfig, BadgesResp.class);
                    if (userBackpacksItemIds.contains(itemConfig.getId())) {
                        badgesResp.setObtain(StatusEnum.STATUS_VALID.getStatus());
                    } else {
                        badgesResp.setObtain(StatusEnum.STATUS_INVALID.getStatus());
                    }

                    badgesResp.setWearing(Optional.ofNullable(user.getItemId())
                            .map(itemId -> {
                                if (itemId.equals(itemConfig.getId())) {
                                    return StatusEnum.STATUS_VALID.getStatus();
                                } else {
                                    return StatusEnum.STATUS_INVALID.getStatus();
                                }
                            })
                            .orElse(StatusEnum.STATUS_INVALID.getStatus()));

                    return badgesResp;
                })
                .sorted(Comparator.comparing(BadgesResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgesResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}