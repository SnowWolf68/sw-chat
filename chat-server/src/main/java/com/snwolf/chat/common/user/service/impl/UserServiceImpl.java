package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.exception.BusinessException;
import com.snwolf.chat.common.common.utils.RequestHolder;
import com.snwolf.chat.common.user.dao.UserBackpackDao;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.enums.ItemTypeEnum;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import com.snwolf.chat.common.user.service.UserService;
import com.snwolf.chat.common.user.service.adapter.UserAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Override
    @Transactional
    public Long register(User user) {
        userDao.save(user);
        return user.getId();
    }

    @Override
    public UserInfoResp getUserInfo() {
        Long uid = RequestHolder.getUserInfo().getUid();
        User user = userDao.getById(uid);
        Integer count = userBackpackDao.getCountByValidItemId(uid, ItemTypeEnum.MODIFY_NAME_CARD.getType());
        return UserAdapter.buildUserInfo(user, count);
    }

    @Override
    public void modifyName(String name) {
        User oldUser = userDao.getByName(name);
        if(ObjectUtil.isNotNull(oldUser)){
            // 用户名已存在
            throw new BusinessException("用户名已存在");
        }
    }
}
