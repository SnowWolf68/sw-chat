package com.snwolf.chat.common.user.service.adapter;

import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.user.domain.entity.Room;
import com.snwolf.chat.common.user.domain.entity.RoomFriend;
import com.snwolf.chat.common.user.domain.enums.RoomTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description:
 */
public class RoomAdapter {


    public static final String SEPARATOR = ",";

    /**
     * 将两个uid按照从小到大的顺序拼接成一个roomKey, 中间使用逗号分隔
     * @param uids
     * @return
     */
    public static String buildRoomKey(List<Long> uids) {
        return uids.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Room buildRoomByType(RoomTypeEnum roomTypeEnum) {
        return Room.builder()
                .type(roomTypeEnum.getType())
                .build();
    }

    /**
     * 通过roomId和uids创建房间
     * @param id: room_id
     * @param uids: 用户id
     * @return
     */
    public static RoomFriend buildRoom(Long id, List<Long> uids) {
        uids.sort(Long::compare);
        return RoomFriend.builder()
                .roomId(id)
                .uid1(uids.get(0))
                .uid2(uids.get(1))
                .roomKey(buildRoomKey(uids))
                .status(StatusEnum.STATUS_VALID.getStatus())
                .build();
    }
}
