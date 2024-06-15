package com.snwolf.chat.common.chat.service.adapter;

import com.snwolf.chat.common.chat.domain.dto.PushMessageDTO;
import com.snwolf.chat.common.chat.domain.enums.WSPushTypeEnum;
import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
public class PushMessageAdapter {


    public static PushMessageDTO buildPushMessage(WSBaseResp<?> msg) {
        return PushMessageDTO.builder()
                .wsBaseMsg(msg)
                .pushType(WSPushTypeEnum.ALL.getType())
                .build();
    }

    public static PushMessageDTO buildPushMessage(WSBaseResp<?> msg, Long uid) {
        return PushMessageDTO.builder()
                .wsBaseMsg(msg)
                .pushType(WSPushTypeEnum.USER.getType())
                .uid(uid)
                .build();
    }
}
