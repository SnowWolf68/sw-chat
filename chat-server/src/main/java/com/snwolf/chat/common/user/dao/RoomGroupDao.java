package com.snwolf.chat.common.user.dao;

import com.snwolf.chat.common.user.domain.entity.RoomGroup;
import com.snwolf.chat.common.user.mapper.RoomGroupMapper;
import com.snwolf.chat.common.user.service.IRoomGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-05
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> implements IRoomGroupService {

}