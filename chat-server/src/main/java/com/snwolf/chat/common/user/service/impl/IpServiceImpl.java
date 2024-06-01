package com.snwolf.chat.common.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.snwolf.chat.common.user.dao.UserDao;
import com.snwolf.chat.common.user.domain.entity.IpDetail;
import com.snwolf.chat.common.user.domain.entity.IpInfo;
import com.snwolf.chat.common.user.domain.entity.User;
import com.snwolf.chat.common.user.domain.vo.resp.TaoBaoIpResp;
import com.snwolf.chat.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService , DisposableBean {

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

    private static IpDetail tryGetIpDetailOrNullThreeTimes(String ip) {
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

    private static IpDetail getIpDetailOrNull(String ip) {
        try {
            String url = "https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc";
            String respData = HttpUtil.get(url);
            TaoBaoIpResp result = JSONUtil.toBean(respData, TaoBaoIpResp.class);
            return result.getData();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 测试获取ip详情的吞吐量
     * @param args
     */
    public static void main(String[] args) {
        LocalDateTime begin = LocalDateTime.now();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.execute(() -> {
                IpDetail ipDetail = tryGetIpDetailOrNullThreeTimes("124.221.54.150");
                if(ObjectUtil.isNotNull(ipDetail)){
                    System.out.println("第" + finalI + "次成功, 目前耗时: " + Duration.between(begin, LocalDateTime.now()).toMillis());
                }
            });
        }
    }

    /**
     * executor线程池的优雅停机配置
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {//最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", executor);
            }
        }
    }
}
