package com.snwolf.chat.common.chat.domain.vo.req.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextMsgReq {

    @NotBlank(message = "内容不能为空")
    @Size(max = 1024, message = "消息内容过长，服务器扛不住啊，兄dei")
    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("回复的消息id,如果没有别传就好")
    private Long replyMsgId;

    @ApiModelProperty("艾特的uid")
    @Size(max = 10, message = "一次别艾特这么多人")
    private List<Long> atUidList;
}
