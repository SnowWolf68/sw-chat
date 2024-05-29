package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.ItemConfig;
import com.snwolf.chat.common.user.mapper.ItemConfigMapper;
import com.snwolf.chat.common.user.service.IItemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-29
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

}
