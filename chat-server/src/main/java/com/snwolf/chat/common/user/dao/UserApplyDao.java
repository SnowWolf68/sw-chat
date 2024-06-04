package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.user.mapper.UserApplyMapper;
import com.snwolf.chat.common.user.service.UserApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-04
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> implements UserApplyService {

}
