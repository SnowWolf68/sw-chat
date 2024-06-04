package com.snwolf.chat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserFriend;
import com.snwolf.chat.common.websocket.domain.vo.response.ChatMemberResp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemberAdapter {


    /**
     * 按照friendPage中的顺序, 重新排列userList中的信息, 并且封装成{@code List<ChatMemberResp>}进行返回
     * @param friendPage
     * @param userList
     * @return
     */
    public static List<ChatMemberResp> buildMemberWithOrder(CursorPageBaseResp<UserFriend> friendPage, List<User> userList){
        Map<Long, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        return friendPage.getList().stream()
                .map(UserFriend::getFriendUid)
                .map(friendId -> {
                    ChatMemberResp chatMemberResp = BeanUtil.copyProperties(userMap.get(friendId), ChatMemberResp.class);
                    chatMemberResp.setUid(friendId);
                    return chatMemberResp;
                })
                .collect(Collectors.toList());
    }
}
