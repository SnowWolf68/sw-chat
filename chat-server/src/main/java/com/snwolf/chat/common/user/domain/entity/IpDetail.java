package com.snwolf.chat.common.user.domain.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class IpDetail implements Serializable {

    private String ip;
    private String isp;
    private String isp_id;
    private String city;
    private String city_id;
    private String country;
    private String country_id;
    private String region;
    private String region_id;
}