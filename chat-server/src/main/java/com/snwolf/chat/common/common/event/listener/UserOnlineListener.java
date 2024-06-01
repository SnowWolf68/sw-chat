package com.snwolf.chat.common.common.event.listener;

import com.snwolf.chat.common.common.event.UserOnlineEvent;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.UserActiveStatusEnum;
import com.snwolf.chat.common.user.service.IpService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserOnlineListener {

    @Resource
    private UserDao userDao;

    @Resource
    private IpService ipService;

    /**
     * 解析ip
     * @param event
     * @throws Throwable
     */
    @EventListener(value = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) throws Throwable {
        User user = event.getUser();
        User updateUser = User.builder()
                .id(user.getId())
                .lastOptTime(user.getLastOptTime())
                .ipInfo(user.getIpInfo())
                .activeStatus(UserActiveStatusEnum.ONLINE.getStatus())
                .build();
        userDao.updateById(updateUser);

        // 解析用户ip
        ipService.refreshIpDetailAsync(user.getId());
    }
}