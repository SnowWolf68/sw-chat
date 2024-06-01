package com.snwolf.chat.common.user.domain.entity;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

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

    /**
     * 最新登录时ip详情
     */
    private IpDetail lastIpDetail;


    public void refreshIp(String ip) {
        if(StrUtil.isBlank(ip)){
            return;
        }
        if(StrUtil.isBlank(createIp)){
            createIp = ip;
        }
        lastIp = ip;
    }

    /**
     * 判断是否需要刷新ip详情
     * @return
     */
    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(lastIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> ObjectUtil.equal(lastIp, ip))
                .isPresent();
        return notNeedRefresh ? null : lastIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if(ObjectUtil.equal(createIp, ipDetail.getIp())){
            createIpDetail = ipDetail;
        }
        if(ObjectUtil.equal(lastIp, ipDetail.getIp())){
            lastIpDetail = ipDetail;
        }
    }
}
