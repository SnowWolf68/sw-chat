package com.snwolf.chat.common.user.service;

import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-27
 */
public interface UserService {

    Long register(User user);

    UserInfoResp getUserInfo();
}
