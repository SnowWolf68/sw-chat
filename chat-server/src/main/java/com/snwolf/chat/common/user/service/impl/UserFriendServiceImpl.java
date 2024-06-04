package com.snwolf.chat.common.user.service.impl;

import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.dao.UserFriendDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserFriend;
import com.snwolf.chat.common.user.service.UserFriendService;
import com.snwolf.chat.common.user.service.adapter.MemberAdapter;
import com.snwolf.chat.common.websocket.domain.vo.response.ChatMemberResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFriendServiceImpl implements UserFriendService {

    @Resource
    private UserFriendDao userFriendDao;

    @Resource
    private UserDao userDao;

    @Override
    public CursorPageBaseResp<ChatMemberResp> frientList(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        // 获取游标查询出来的UserFriend列表
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getCursorPage(uid, cursorPageBaseReq);
        if(friendPage.isEmpty()){
            return CursorPageBaseResp.empty();
        }
        // 根据分页查询出来的UserFriend列表, 填充ChatMemberResp所需的数据
        List<Long> friendIds = friendPage.getList().stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> userList = userDao.listByIds(friendIds);
        return CursorPageBaseResp.init(friendPage, MemberAdapter.buildMemberWithOrder(friendPage, userList));
    }
}
