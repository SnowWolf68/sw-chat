package com.snwolf.chat.common.user.service.impl;

import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.common.utils.CursorUtils;
import com.snwolf.chat.common.user.dao.UserApplyDao;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.dao.UserFriendDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.user.domain.entity.UserFriend;
import com.snwolf.chat.common.user.domain.vo.req.FriendApplyReq;
import com.snwolf.chat.common.user.domain.vo.req.FriendCheckReq;
import com.snwolf.chat.common.user.domain.vo.resp.FriendCheckResp;
import com.snwolf.chat.common.user.service.UserFriendService;
import com.snwolf.chat.common.user.service.adapter.MemberAdapter;
import com.snwolf.chat.common.user.service.adapter.UserApplyAdapter;
import com.snwolf.chat.common.websocket.domain.vo.response.ChatMemberResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFriendServiceImpl implements UserFriendService {

    @Resource
    private UserFriendDao userFriendDao;

    @Resource
    private UserDao userDao;

    @Resource
    private UserApplyDao userApplyDao;

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

    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq friendCheckReq) {
        // 查询出uid的在friendCheckReq中的所有好友信息
        List<UserFriend> friendList = userFriendDao.getByFriends(uid, friendCheckReq.getUidList());
        // 根据查询出来的好友信息构造返回值
        Set<Long> friendSet = friendList.stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> collect = friendCheckReq.getUidList()
                .stream()
                .map(id -> {
                    FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
                    friendCheck.setUid(id);
                    friendCheck.setIsFriend(friendSet.contains(id));
                    return friendCheck;
                })
                .collect(Collectors.toList());
        return new FriendCheckResp(collect);
    }

    @Override
    public void apply(Long uid, FriendApplyReq friendApplyReq) {
        // 查询两人是否已经是好友
        UserFriend oldFriend = userFriendDao.getByUidAndFriendUid(uid, friendApplyReq.getTargetUid());
        AssertUtil.isEmpty(oldFriend, "Ta已经是您的好友了, 无需重复添加");
        // 查询之前是否有未处理的好友申请
        UserApply oldApply = userApplyDao.getByUidAndFriendUid(uid, friendApplyReq.getTargetUid());
        AssertUtil.isEmpty(oldApply, "您已发送过好友申请, 请耐心等待对方处理");
        // 可以进行好友申请
        UserApply userApply = UserApplyAdapter.buildApply(uid, friendApplyReq);
        userApplyDao.save(userApply);
    }
}
