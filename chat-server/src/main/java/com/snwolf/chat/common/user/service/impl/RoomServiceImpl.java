package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.user.dao.RoomDao;
import com.snwolf.chat.common.user.dao.RoomFriendDao;
import com.snwolf.chat.common.user.domain.entity.Room;
import com.snwolf.chat.common.user.domain.entity.RoomFriend;
import com.snwolf.chat.common.user.domain.enums.RoomTypeEnum;
import com.snwolf.chat.common.user.service.RoomService;
import com.snwolf.chat.common.user.service.adapter.RoomAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private RoomDao roomDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uids) {
        // 首先判断uids是否符合要求
        AssertUtil.isNotEmpty(uids, "房间创建失败, 至少需要两位好友才能创建房间");
        AssertUtil.equal(uids.size(), 2, "房间创建失败, 至少需要两位好友才能创建房间");
        // 拼接room_key
        String roomKey = RoomAdapter.buildRoomKey(uids);
        // 判断这个房间之前是否被创建过
        RoomFriend oldRoomFriend = roomFriendDao.getByRoomKey(roomKey);
        if(ObjectUtil.isNotNull(oldRoomFriend)){
            // 如果之前创建过(恢复好友场景), 那么直接恢复
            restoreRoomFriend(oldRoomFriend);
            return oldRoomFriend;
        }else{
            // 如果之前没有被创建过, 那么创建, 同时向root表和room_friend表插入两条数据
            Room room = roomDao.createRoom(RoomTypeEnum.FRIEND);
            return roomFriendDao.createRoomFriend(room.getId(), uids);
        }

    }

    @Override
    public void disableFriendRoom(List<Long> ids) {
        roomFriendDao.disableFriendRoom(ids);
    }

    /**
     * 恢复房间
     * @param oldRoomFriend
     */
    private void restoreRoomFriend(RoomFriend oldRoomFriend) {
        roomFriendDao.restoreRoomFriend(oldRoomFriend.getId());
    }
}
