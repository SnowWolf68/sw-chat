package com.snwolf.chat.common.user.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.snwolf.chat.common.user.domain.entity.UserFriend;
import com.snwolf.chat.common.user.mapper.UserFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-06-04
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    public CursorPageBaseResp<UserFriend> getCursorPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        // select * from user_friend where id < #{cursor} and uid = #{uid} limit 0, #{pageSize} order by id desc;
        Page<UserFriend> pageResult = lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .lt(StrUtil.isNotBlank(cursorPageBaseReq.getCursor()), UserFriend::getId, cursorPageBaseReq.getCursor())
                .orderByDesc(UserFriend::getId)
                .page(new Page<>(0, cursorPageBaseReq.getPageSize(), false));
        String cursor = Optional.ofNullable(CollectionUtil.getLast(pageResult.getRecords()))
                .map(UserFriend::getId)
                .map(String::valueOf)
                .orElse(null);
        Boolean isLast = pageResult.getRecords().size() < cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, pageResult.getRecords());
    }
}