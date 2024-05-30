package com.snwolf.chat.common.user.domain.vo.resp;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgesResp {
    @ApiModelProperty("徽章id")
    private Long id;
    @ApiModelProperty("徽章图表")
    private String img;
    @ApiModelProperty("徽章描述")
    private String describe;
    @ApiModelProperty("是否拥有徽章 0否 1是")
    private Integer obtain;
    @ApiModelProperty("是否佩戴 0否 1是")
    private Integer wearing;
}
