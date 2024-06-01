package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.UserRole;
import com.snwolf.chat.common.user.mapper.UserRoleMapper;
import com.snwolf.chat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-01
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
