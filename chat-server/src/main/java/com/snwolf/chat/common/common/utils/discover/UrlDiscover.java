package com.snwolf.chat.common.common.utils.discover;

import com.snwolf.chat.common.common.utils.discover.domain.UrlInfo;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description:
 */
public interface UrlDiscover {

    /**
     * 获取文本content中的所有url的信息, 异步并行解析
     * @param content: 文本
     * @Return: {@code <url, urlInfo>}, 由于文本content中可能包含多个url, 因此使用map的方式返回
     */
    Map<String, UrlInfo> getUrlContentMapAsync(String content);

    /**
     * 获取文本content中的所有url的信息, 串行解析
     * @param content: 文本
     * @Return: {@code <url, urlInfo>}, 由于文本content中可能包含多个url, 因此使用map的方式返回
     */
    Map<String, UrlInfo> getUrlContentMap(String content);

    /**
     * 获取html中的UrlInfo的信息
     * @param url: 要获取的url
     * @return
     */
    UrlInfo getUrlInfo(String url);

    /**
     * 获取html中的标题
     * @param document
     * @return
     */
    String getTitle(Document document);

    /**
     * 获取html中的描述(description)
     * @param document
     * @return
     */
    String getDescription(Document document);

    /**
     * 获取html中的图片
     * @param document
     * @return
     */
    String getImage(String url, Document document);
}
