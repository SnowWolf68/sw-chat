package com.snwolf.chat.common.user.service.adapter;

import com.snwolf.chat.common.user.domain.dto.ItemInfoDTO;
import com.snwolf.chat.common.user.domain.entity.ItemConfig;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
public class ItemInfoAdapter {

    public static ItemInfoDTO buildItemInfo(ItemConfig itemConfig){
        return ItemInfoDTO.builder()
                .itemId(itemConfig.getId())
                .needRefresh(Boolean.TRUE)
                .img(itemConfig.getImg())
                .describe(itemConfig.getDescribe())
                .build();
    }
}
