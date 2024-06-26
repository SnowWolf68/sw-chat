package com.snwolf.chat.common.user.service;

import com.snwolf.chat.common.user.domain.dto.ItemInfoDTO;
import com.snwolf.chat.common.user.domain.dto.SummaryInfoDTO;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.vo.req.ItemInfoReq;
import com.snwolf.chat.common.user.domain.vo.req.SummaryInfoReq;
import com.snwolf.chat.common.user.domain.vo.resp.BadgesResp;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @since 2024-05-27
 */
public interface UserService {

    Long register(User user);

    UserInfoResp getUserInfo();

    void modifyName(Long uid, String name);

    List<BadgesResp> badges(Long uid);

    void wearingBagde(Long uid, Long itemId);

    void black(Long id);

    List<SummaryInfoDTO> getSummaryUserInfo(SummaryInfoReq summaryInfoReq);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq itemInfoReq);
}
