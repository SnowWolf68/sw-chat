package com.snwolf.chat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/4/2024
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendUnreadResp {

    @ApiModelProperty("申请列表的未读数")
    private Integer unReadCount;
}
