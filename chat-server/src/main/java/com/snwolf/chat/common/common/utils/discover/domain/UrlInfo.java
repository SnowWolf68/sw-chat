package com.snwolf.chat.common.common.utils.discover.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlInfo {

    /**
     * 标题
     **/
    private String title;

    /**
     * 描述
     **/
    private String description;

    /**
     * 网站LOGO
     **/
    private String image;
}
