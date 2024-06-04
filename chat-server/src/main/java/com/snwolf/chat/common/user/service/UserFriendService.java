package com.snwolf.chat.common.user.service;

import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.user.domain.vo.req.FriendApplyReq;
import com.snwolf.chat.common.user.domain.vo.req.FriendCheckReq;
import com.snwolf.chat.common.user.domain.vo.resp.FriendCheckResp;
import com.snwolf.chat.common.websocket.domain.vo.response.ChatMemberResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-04
 */
public interface UserFriendService {

    CursorPageBaseResp<ChatMemberResp> frientList(Long uid, CursorPageBaseReq cursorPageBaseReq);

    FriendCheckResp check(Long uid, FriendCheckReq friendCheckReq);

    void apply(Long uid, FriendApplyReq friendApplyReq);
}
