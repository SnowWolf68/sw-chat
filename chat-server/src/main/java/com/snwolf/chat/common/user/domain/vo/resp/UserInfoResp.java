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
public class UserInfoResp {
    @ApiModelProperty("uid")
    private Long id;
    @ApiModelProperty("用户名称")
    private String name;
    @ApiModelProperty("用户头像")
    private String avatar;
    @ApiModelProperty("性别")
    private Integer sex;
    @ApiModelProperty("剩余改名次数")
    private Integer modifyNameChance;
}
