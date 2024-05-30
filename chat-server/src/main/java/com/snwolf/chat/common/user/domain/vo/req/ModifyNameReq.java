package com.snwolf.chat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ModifyNameReq {

    @ApiModelProperty("新的用户名")
    @NotBlank
    @Length(max = 6, message = "用户名不可以取太长")
    private String name;
}
