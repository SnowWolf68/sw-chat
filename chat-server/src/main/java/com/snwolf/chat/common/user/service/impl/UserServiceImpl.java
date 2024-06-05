package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
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
import com.snwolf.chat.common.user.domain.dto.ItemInfoDTO;
import com.snwolf.chat.common.user.domain.dto.SummaryInfoDTO;
import com.snwolf.chat.common.user.domain.entity.Black;
import com.snwolf.chat.common.user.domain.entity.ItemConfig;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.entity.UserBackpack;
import com.snwolf.chat.common.user.domain.enums.BlackTypeEnum;
import com.snwolf.chat.common.user.domain.enums.ItemTypeEnum;
import com.snwolf.chat.common.user.domain.vo.req.ItemInfoReq;
import com.snwolf.chat.common.user.domain.vo.req.SummaryInfoReq;
import com.snwolf.chat.common.user.domain.vo.resp.BadgesResp;
import com.snwolf.chat.common.user.domain.vo.resp.UserInfoResp;
import com.snwolf.chat.common.user.service.UserService;
import com.snwolf.chat.common.user.service.adapter.ItemInfoAdapter;
import com.snwolf.chat.common.user.service.adapter.UserAdapter;
import com.snwolf.chat.common.user.service.cache.ItemCache;
import com.snwolf.chat.common.user.service.cache.UserCache;
import com.snwolf.chat.common.user.service.cache.batchCacheWithFramework.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserBackpackDao userBackpackDao;

    @Resource(name = "ItemCache")
    private ItemCache itemCache;

    @Resource
    private ItemConfigDao itemConfigDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private BlackDao blackDao;

    @Resource
    private UserCache userCache;

    @Resource
    private UserSummaryCache userSummaryCache;

    @Resource(name = "BatchFrameworkItemCache")
    private com.snwolf.chat.common.user.service.cache.batchCacheWithFramework.ItemCache frameworkItemCache;

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

    @Override
    public List<SummaryInfoDTO> getSummaryUserInfo(SummaryInfoReq summaryInfoReq) {
        // 首先判断req中有哪些uid是需要刷新的
        List<Long> needIds = getNeedRefreshUids(summaryInfoReq);
        // 拿着这些id去查询用户信息, 由于有些id需要刷新, 有些id不需要刷新, 所以这里getBatch需要返回一个<id, summaryInfoDTO>的map
        // 这样便于后面封装返回值的时候, 拿着req中的每一条记录来这个map中查询, 如果有记录, 说明需要刷新, 反之说明不需要刷新
        Map<Long, SummaryInfoDTO> summaryInfoDTOMap = userSummaryCache.getBatch(needIds);
        // 封装返回值
        return buildSummaryResp(summaryInfoReq, summaryInfoDTOMap);
    }

    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq itemInfoReq) {
        // 简单实现, 由于徽章数目比较少, 因此这里首先获取到所有徽章信息, 然后再结合req中的lastModifyTime进行判断
        List<Long> itemIdList = itemInfoReq.getReqList().stream()
                .map(ItemInfoReq.InfoReq::getItemId)
                .collect(Collectors.toList());
        Map<Long, ItemConfig> itemMap = frameworkItemCache.getBatch(itemIdList);
        return itemInfoReq.getReqList().stream()
                .map(infoReq -> {
                    ItemConfig itemConfig = itemMap.get(infoReq.getItemId());
                    if (ObjectUtil.isNull(infoReq.getLastModifyTime()) ||
                            (itemConfig.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > infoReq.getLastModifyTime())) {
                        // 需要refresh
                        return ItemInfoAdapter.buildItemInfo(itemConfig);
                    } else {
                        // 无需refresh
                        return ItemInfoDTO.empty(infoReq.getItemId());
                    }
                })
                .collect(Collectors.toList());
    }

    private List<SummaryInfoDTO> buildSummaryResp(SummaryInfoReq request, Map<Long, SummaryInfoDTO> summaryInfoDTOMap) {
        return request.getReqList().stream()
                .map(infoReq -> summaryInfoDTOMap.containsKey(infoReq.getUid()) ? summaryInfoDTOMap.get(infoReq.getUid()) : SummaryInfoDTO.empty(infoReq.getUid()))
                .collect(Collectors.toList());
    }

    /**
     * 获取需要刷新的用户id
     *  这里判断是否需要刷新的依据是前端传过来的lastModifyTime和后端的lastModifyTime的关系
     *  如果前端的lastModifyTime小于后端的lastModifyTime, 那么说明后端在这个期间内, 发生过修改, 此时就需要给前端传刷新后的用户信息
     *  反之说明后端的数据在这段时间内没有发生过修改, 不需要更新前端数据
     * <p>其中, 后端的lastModifyTime是在Redis中存储的
     * @param request
     * @return
     */
    private List<Long> getNeedRefreshUids(SummaryInfoReq request) {
        // 从Redis中获取所有lastModifyTime, 这里为了方便比较, 我们将time都使用毫秒值long来处理
        List<Long> lastModityTimeList = UserCache.getLastModifyTimeByIds(
                request.getReqList().stream().map(SummaryInfoReq.InfoReq::getUid).collect(Collectors.toList()));
        // 和前端request中的lastModifyTime进行对比, 筛选出来需要进行刷新的uid
        List<Long> needRefreshIdList = new ArrayList<>();
        for (int i = 0; i < request.getReqList().size(); i++) {
            Long lastModifyTime = request.getReqList().get(i).getLastModifyTime();
            Long userModifyTime = lastModityTimeList.get(i);
            if(ObjectUtil.isNull(lastModifyTime) || (ObjectUtil.isNotNull(userModifyTime) && lastModifyTime < userModifyTime)){
                needRefreshIdList.add(request.getReqList().get(i).getUid());
            }
        }
        return needRefreshIdList;
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
