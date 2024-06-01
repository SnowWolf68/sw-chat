package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.Role;
import com.snwolf.chat.common.user.mapper.RoleMapper;
import com.snwolf.chat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-01
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
