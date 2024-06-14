package com.snwolf.chat.common.common.utils.discover;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.snwolf.chat.common.common.utils.completableFuture.FutureUtils;
import com.snwolf.chat.common.common.utils.discover.domain.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description: 模版设计模式, 抽取责任链中的公共方法
 */
@Slf4j
public abstract class AbstractUrlDiscover implements UrlDiscover {


    /**
     * 匹配url的正则表达式
     */
    private static final Pattern PATTERN = Pattern.compile("((http|https)://)?(www.)?([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

    @Override
    public UrlInfo getUrlInfo(String url) {
        try {
            Connection connect = Jsoup.connect(url);
            // 设置超时时间, 如果某一个url请求时间过长, 及时熔断
            connect.timeout(2000);
            Document document = connect.get();
            try {
                return UrlInfo.builder()
                        .title(getTitle(document))
                        .description(getDescription(document))
                        .image(getImage(url, document))
                        .build();
            } catch (Exception e) {
                log.error("获取url信息失败: {}", url, e);
                return null;
            }
        } catch (IOException e) {
            log.error("请求url失败: {}", url, e);
            return null;
        }
    }


    @Override
    public Map<String, UrlInfo> getUrlContentMapAsync(String content) {
        // 通过正则匹配, 得到content中所有的url
        List<String> urlList = getUrlList(content);
        List<CompletableFuture<Pair<String, UrlInfo>>> completableFutureList = urlList.stream()
                // 对于每个url, 异步并行获取UrlInfo
                .map(url -> CompletableFuture.supplyAsync(() -> {
                    UrlInfo urlInfo = getUrlInfo(url);
                    return ObjectUtil.isNotNull(urlInfo) ? Pair.of(url, urlInfo) : null;
                }))
                .collect(Collectors.toList());
        // 使用美团技术文章中的工具类整合多个CompletableFuture的结果
        CompletableFuture<List<Pair<String, UrlInfo>>> listCompletableFuture = FutureUtils.sequenceNonNull(completableFutureList);
        // 组装成map
        return listCompletableFuture.join().stream()
                // 其中有可能存在相同的url, 因此收集到map中时需要设置 key冲突时 的合并规则
                // (v1, v2) -> v1 表示如果两个<k1, v1>, <k2, v2> 的key相同, 即k1 == k2, 那么此时对于k1(k2)这个key, 只保存v1这个value
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (v1, v2) -> v1));
    }


    @Override
    public Map<String, UrlInfo> getUrlContentMap(String content) {
        List<String> urlList = getUrlList(content);
        return urlList.stream()
                .map(url -> {
                    UrlInfo urlInfo = getUrlInfo(url);
                    return ObjectUtil.isNotNull(urlInfo) ? Pair.of(url, urlInfo) : null;
                })
                .filter(ObjectUtil::isNotNull)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (v1, v2) -> v1));
    }

    private List<String> getUrlList(String content) {
        // 使用hutool工具类封装过的正则匹配方法
        List<String> urlList = ReUtil.findAll(PATTERN, content, 0);
        // 有些url前面没有http/https前缀, 这里统一进行一次过滤, 如果没有就加上
        return urlList.stream()
                .map(url -> {
                    if (!StrUtil.startWith(url, "http")) {
                        return "http://" + url;
                    }
                    return url;
                })
                .collect(Collectors.toList());
    }

    /**
     * 判断链接是否有效
     * @param: 输入链接
     * @Return: 返回true或者false
     */
    public static boolean isConnect(String href) {
        //请求地址
        URL url;
        //请求状态码
        int state;
        //下载链接类型
        String fileType;
        try {
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            //如果成功200，缓存304，移动302都算有效链接，并且不是下载链接
            if ((state == 200 || state == 302 || state == 304) && fileType == null) {
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
