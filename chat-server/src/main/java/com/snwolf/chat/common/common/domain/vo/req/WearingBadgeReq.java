package com.snwolf.chat.common.common.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("佩戴徽章请求")
public class WearingBadgeReq {

    @ApiModelProperty("徽章id")
    @NotNull
    private Long itemId;
}