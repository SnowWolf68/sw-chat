package com.snwolf.chat.common.user.service;


import com.snwolf.chat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-01
 */
public interface RoleService {

    /**
     * 是否拥有某个权限(角色)
     * @param uid
     * @param roleEnum
     * @return
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
