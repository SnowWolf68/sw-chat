package com.snwolf.chat.common.chat.domain.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
public class ChatMessageResp {

    @ApiModelProperty("发送者信息")
    private UserInfo fromUser;
    @ApiModelProperty("消息详情")
    private Message message;

    @Data
    @Builder
    public static class UserInfo {
        @ApiModelProperty("用户id")
        private Long uid;
    }

    @Data
    @Builder
    public static class Message {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("房间id")
        private Long roomId;
        @ApiModelProperty("消息发送时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime sendTime;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容不同的消息类型，内容体不同，见https://www.yuque.com/snab/mallcaht/rkb2uz5k1qqdmcmd")
        private Object body;
        @ApiModelProperty("消息标记")
        private MessageMark messageMark;
    }

    @Data
    @Builder
    public static class MessageMark {
        @ApiModelProperty("点赞数")
        private Integer likeCount;
        @ApiModelProperty("该用户是否已经点赞 0否 1是")
        private Integer userLike;
        @ApiModelProperty("举报数")
        private Integer dislikeCount;
        @ApiModelProperty("该用户是否已经举报 0否 1是")
        private Integer userDislike;
    }
}
