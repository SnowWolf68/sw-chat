package com.snwolf.chat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

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
public class FriendCheckReq {

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty("校验好友的uid")
    private List<Long> uidList;
}
