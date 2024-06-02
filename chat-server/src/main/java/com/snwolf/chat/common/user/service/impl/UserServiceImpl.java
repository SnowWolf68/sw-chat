package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.snwolf.chat.common.common.annotation.RedissonLockAnno;
import com.snwolf.chat.common.common.event.UserBlackEvent;
import com.snwolf.chat.common.common.event.UserRegisterEvent;
import com.snwolf.chat.common.common.utils.AssertUtil;
import com.snwolf.chat.common.common.utils.RequestHolder;
import com.snwolf.chat.common.user.dao.BlackDao;
import com.snwolf.chat.common.user.dao.ItemConfigDao;
import com.snwolf.chat.common.user.dao.UserBackpackDao;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.Black;
import com.snwolf.chat.common.user.domain.entity.ItemConfig;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.domain.enums.BlackTypeEnum;
import com.snwolf.chat.common.user.domain.enums.ItemTypeEnum;
import com.snwolf.chat.common.user.domain.vo.resp.BadgesResp;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import com.snwolf.chat.common.user.service.UserService;
import com.snwolf.chat.common.user.service.adapter.UserAdapter;
import com.snwolf.chat.common.user.service.cache.ItemCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Resource
    private ItemCache itemCache;

    @Resource
    private ItemConfigDao itemConfigDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private BlackDao blackDao;

    @Override
    @Transactional
    public Long register(User user) {
        userDao.save(user);
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
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
    @Transactional(rollbackFor = Exception.class)
    @RedissonLockAnno(lockKey = "#uid")
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "用户名已存在");
        UserBackpack modifyNameItem = userBackpackDao.getFirstUnusedItem(uid, ItemTypeEnum.MODIFY_NAME_CARD.getType());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡已用完");
        // 开始改名
        boolean result = userBackpackDao.useItem(modifyNameItem);
        if(result){
            // 改名
            userDao.modifyName(uid, name);
            // todo: 删除缓存
        }
    }

    @Override
    public List<BadgesResp> badges(Long uid) {
        // 查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户拥有的徽章
        List<UserBackpack> userBackpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 查询当前用户佩戴的徽章
        User user = userDao.getById(uid);
        // 组装返回值
        return UserAdapter.buildBadgesResp(itemConfigs, userBackpacks, user);
    }

    @Override
    public void wearingBagde(Long uid, Long itemId) {
        // 确保这个物品是徽章
        ItemConfig itemConfig = itemConfigDao.getById(itemId);
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该物品不是徽章");
        // 确保当前用户有该徽章
        UserBackpack userBackpack = userBackpackDao.getFirstItem(uid, itemId);
        AssertUtil.isNotEmpty(userBackpack, "您还没有该徽章, 快去获得吧");
        // 查询用户是不是已经佩戴了该徽章
        User user = userDao.getById(uid);
        AssertUtil.notEqual(user.getItemId(), itemId, "您已经佩戴了该徽章, 请换一个佩戴吧");
        userDao.wearingBadge(uid, itemId);
    }

    /**
     * 同时拉黑用户的id和ip
     * @param id: 要拉黑的id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(Long id) {
        // 拉黑该用户的id
        Black black = Black.builder()
                .type(BlackTypeEnum.UID.getType())
                .target(id.toString())
                .build();
        try {
            blackDao.save(black);
        } catch (DuplicateKeyException e) {
            log.info("该id: " + id + " 已经被拉黑过, 无需重复拉黑");
        }
        // 拉黑该用户的ip
        User targetUser = userDao.getById(id);
        blackIp(targetUser.getIpInfo().getCreateIp());
        blackIp(targetUser.getIpInfo().getLastIp());
        // todo: 给前端通知一个拉黑事件
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, targetUser));
    }

    private void blackIp(String ip) {
        if(StrUtil.isBlank(ip)){
            return;
        }
        Black black = Black.builder()
                .type(BlackTypeEnum.IP.getType())
                .target(ip)
                .build();
        try {
            blackDao.save(black);
        } catch (DuplicateKeyException e) {
            log.info("该ip: " + ip + " 已经被拉黑过, 无需重复拉黑");
        }
    }
}
