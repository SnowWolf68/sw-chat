package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.snwolf.chat.common.common.domain.vo.resp.ApiResult;
import com.snwolf.chat.common.common.utils.JsonUtils;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.IpDetail;
import com.snwolf.chat.common.user.domain.entity.IpInfo;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.vo.resp.TaoBaoIpResp;
import com.snwolf.chat.common.user.service.IpService;
import io.github.classgraph.json.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService {

    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(500), new NamedThreadFactory("refresh-ipDetail", false));

    @Resource
    private UserDao userDao;

    @Override
    public void refreshIpDetailAsync(Long uid) {
        executor.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (ObjectUtil.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.needRefreshIp();
            if (StrUtil.isBlank(ip)) {
                return;
            }
            // refresh
            IpDetail ipDetail = tryGetIpDetailOrNullThreeTimes(ip);
            if(ObjectUtil.isNotNull(ipDetail)){
                ipInfo.refreshIpDetail(ipDetail);
                User newUser = User.builder()
                        .id(uid)
                        .ipInfo(ipInfo)
                        .build();
                userDao.updateById(newUser);
            }
        });
    }

    private IpDetail tryGetIpDetailOrNullThreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (ObjectUtil.isNotNull(ipDetail)) {
                return ipDetail;
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error("tryGetIpDetailOrNullThreeTimes InterruptedException: " + e);
                }
            }
        }
        return null;
    }

    private IpDetail getIpDetailOrNull(String ip) {
        String url = "https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc";
        String respData = HttpUtil.get(url);
        TaoBaoIpResp result = JSONUtil.toBean(respData, TaoBaoIpResp.class);
        return result.getData();
    }
}
