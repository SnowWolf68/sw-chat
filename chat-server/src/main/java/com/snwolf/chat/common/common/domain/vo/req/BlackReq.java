package com.snwolf.chat.common.common.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("拉黑用户请求")
public class BlackReq {

    @ApiModelProperty("要拉黑用户id")
    @NotNull
    private Long id;
}