package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.Black;
import com.snwolf.chat.common.user.mapper.BlackMapper;
import com.snwolf.chat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-01
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
