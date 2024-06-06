package com.snwolf.chat.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("大小（字节）")
    @NotNull
    private Long size;

    @ApiModelProperty("下载地址")
    @NotBlank
    private String url;

    @ApiModelProperty("缩略图宽度（像素）")
    @NotNull
    private Integer thumbWidth;

    @ApiModelProperty("缩略图高度（像素）")
    @NotNull
    private Integer thumbHeight;

    @ApiModelProperty("缩略图大小（字节）")
    @NotNull
    private Long thumbSize;

    @ApiModelProperty("缩略图下载地址")
    @NotBlank
    private String thumbUrl;

}
