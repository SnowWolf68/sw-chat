package com.snwolf.chat.common.user.service.adapter;

import com.snwolf.chat.common.user.domain.dto.SummaryInfoDTO;
import com.snwolf.chat.common.user.domain.entity.User;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
public class SummaryAdapter {

    public static SummaryInfoDTO buildUserSummary(User user) {
        return SummaryInfoDTO.builder()
                .uid(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .wearingItemId(user.getItemId())
                .needRefresh(Boolean.TRUE)
                .build();
    }
}
