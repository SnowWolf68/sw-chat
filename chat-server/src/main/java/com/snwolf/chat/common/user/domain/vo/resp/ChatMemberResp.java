package com.snwolf.chat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 群成员列表的成员信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("群成员列表的成员信息")
public class ChatMemberResp {

    @ApiModelProperty("uid")
    private Long uid;
    /**
     * @see com.snwolf.chat.common.user.domain.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;
    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;
}
