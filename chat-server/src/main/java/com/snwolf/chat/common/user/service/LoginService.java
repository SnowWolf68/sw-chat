package com.snwolf.chat.common.user.service;

public interface LoginService {

    /**
     * 刷新token有效期
     *
     * @param token
     */
    void renewalTokenIfNecessary(String token);

    /**
     * login
     * @param id
     * @return
     */
    String login(Long id);


    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    Long getValidUid(String token);
}