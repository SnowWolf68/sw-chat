package com.snwolf.chat.common.chat.domain.dto;

import com.snwolf.chat.common.websocket.domain.vo.response.WSBaseResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/8/2024
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageDTO {

    /**
     * 要推送的消息内容
     */
    private WSBaseResp<?> wsBaseMsg;

    /**
     * 被推送者的uid, 如果这条消息是全员消息, 那么uid为null
     */
    private Long uid;

    /**
     * 推送类型: 全员 or 个人
     * @see com.snwolf.chat.common.chat.domain.enums.WSPushTypeEnum
     */
    private Integer pushType;
}
