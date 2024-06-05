package com.snwolf.chat.common.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snwolf.chat.common.common.domain.enums.StatusEnum;
import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.req.PageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.common.domain.vo.resp.PageBaseResp;
import com.snwolf.chat.common.common.event.UserApplyEvent;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.user.dao.UserApplyDao;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.dao.UserFriendDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserApply;
import com.snwolf.chat.common.user.domain.entity.UserFriend;
import com.snwolf.chat.common.user.domain.enums.ApplyStatusEnum;
import com.snwolf.chat.common.user.domain.vo.req.FriendApplyReq;
import com.snwolf.chat.common.user.domain.vo.req.FriendApproveReq;
import com.snwolf.chat.common.user.domain.vo.req.FriendCheckReq;
import com.snwolf.chat.common.user.domain.vo.resp.FriendApplyResp;
import com.snwolf.chat.common.user.domain.vo.resp.FriendCheckResp;
import com.snwolf.chat.common.user.domain.vo.resp.FriendUnreadResp;
import com.snwolf.chat.common.user.service.RoomService;
import com.snwolf.chat.common.user.service.UserFriendService;
import com.snwolf.chat.common.user.service.adapter.MemberAdapter;
import com.snwolf.chat.common.user.service.adapter.UserApplyAdapter;
import com.snwolf.chat.common.websocket.domain.vo.response.ChatMemberResp;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoomService roomService;

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
        // 申请事件推送给前端, 即给被申请的好友推送一条申请通知
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, userApply));
    }

    @Override
    public FriendUnreadResp unreadCount(Long uid) {
        // 查询user_apply表, 找当前用户为target_id, 并且状态为未读的记录的数量
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    /**
     * 这里的翻页就是普通的翻页, 即带有 当前页码 和 当前页大小 的分页查询
     *
     * @param uid
     * @param request
     * @return
     */
    @Override
    public PageBaseResp<FriendApplyResp> page(Long uid, PageBaseReq request) {
        // 分页查询当前用户的申请列表
        Page<UserApply> applyPage = userApplyDao.friendApplyPage(uid, request.getPageNo(), request.getPageSize());
        // 通过分页查询结果, 构造返回对象
        List<Long> ids = applyPage.getRecords().stream()
                .map(UserApply::getUid)
                .collect(Collectors.toList());
        List<User> userList = userDao.listByIds(ids);
        Map<Long, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        List<FriendApplyResp> friendApplyRespList = applyPage.getRecords().stream()
                .map(apply -> {
                    FriendApplyResp friendApplyResp = new FriendApplyResp();
                    friendApplyResp.setApplyId(apply.getId());
                    friendApplyResp.setUid(apply.getUid());
                    friendApplyResp.setType(apply.getType());
                    User user = userMap.get(apply.getUid());
                    friendApplyResp.setName(user.getName());
                    friendApplyResp.setAvatar(user.getAvatar());
                    return friendApplyResp;
                })
                .collect(Collectors.toList());
        // 将本页查询出来的所有未读数据都设置为已读
        // 由于markRead方法加了事务, 并且markRead方法属于本地调用, 直接调用会导致事务失效, 因此这里需要取到代理对象, 然后进行调用
        UserFriendService proxy = (UserFriendService) AopContext.currentProxy();
        proxy.markRead(applyPage.getRecords());
        return PageBaseResp.init(request.getPageNo(), request.getPageSize(), applyPage.getTotal(), friendApplyRespList);
    }

    @Override
    @Transactional
    public void markRead(List<UserApply> records) {
        List<Long> ids = records.stream()
                .map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.markReadByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyApprove(Long uid, FriendApproveReq friendApproveReq) {
        // 从申请表中找到对应的申请记录
        UserApply userApply = userApplyDao.getById(friendApproveReq.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        // 判断申请记录中的targetId和当前用户uid是否一致
        AssertUtil.equal(uid, userApply.getTargetId(), "不存在申请记录");
        // 判断当前申请是否还是未同意状态
        AssertUtil.equal(userApply.getStatus(), ApplyStatusEnum.WAIT_APPROVAL, "当前好友申请已同意");
        // 将申请设置为已同意
        userApplyDao.approve(userApply.getId());
        // 创建好友关系, 往user_friend表中插入两条数据
        userFriendDao.createFriend(userApply.getUid(), userApply.getTargetId());
        // 创建两人的聊天房间
        roomService.createFriendRoom(Arrays.asList(userApply.getUid(), userApply.getTargetId()));
        // todo: 在聊天房间中发送一条打招呼的消息
    }
}
