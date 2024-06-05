package com.snwolf.chat.common.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // 空字段不展示到json中, 进一步节省带宽
public class SummaryInfoDTO {

    @ApiModelProperty(value = "用户id")
    private Long uid;
    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;
    @ApiModelProperty(value = "用户昵称")
    private String name;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "归属地")
    private String locPlace;
    @ApiModelProperty("佩戴的徽章id")
    private Long wearingItemId;
    @ApiModelProperty(value = "用户拥有的徽章id列表")
    List<Long> itemIds;

    public static SummaryInfoDTO empty(Long uid){
        return SummaryInfoDTO.builder()
                .uid(uid)
                .needRefresh(Boolean.FALSE)
                .build();
    }
}
