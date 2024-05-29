package com.snwolf.chat.common.user.domain.vo.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResp {
    private Long id;
    private String name;
    private String avatar;
    private Integer sex;
    private Integer modifyNameChance;
}
