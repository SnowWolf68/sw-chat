package com.snwolf.chat.common.user.domain.entity;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpInfo implements Serializable {

    /**
     * 注册时ip
     */
    private String createIp;

    /**
     * 最新登录的ip
     */
    private String lastIp;

    /**
     * 注册时ip详情
     */
    private IpDetail createIpDetail;

    public void refreshIp(String ip) {
        if(StrUtil.isBlank(ip)){
            return;
        }
        if(StrUtil.isBlank(createIp)){
            createIp = ip;
        }
        lastIp = ip;
    }
}
