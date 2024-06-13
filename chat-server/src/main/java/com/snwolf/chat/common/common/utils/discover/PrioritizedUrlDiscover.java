package com.snwolf.chat.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/13/2024
 * @Description: 带有优先级的组装方法: 将责任链中的Discover组装起来, 需要解析的时候按照顺序依次调用
 */
public class PrioritizedUrlDiscover extends AbstractUrlDiscover {

    /**
     * url解析的责任链, 目前暂时只有两个Discover, 所以初始容量为2
     */
    private final List<UrlDiscover> urlDiscoverList = new ArrayList<>(2);


    public PrioritizedUrlDiscover(){
        urlDiscoverList.add(new CommonUrlDiscover());
        urlDiscoverList.add(new WxUrlDiscover());
    }


    @Override
    public String getTitle(Document document) {
        for (UrlDiscover discover : urlDiscoverList) {
            String title = discover.getTitle(document);
            if(StrUtil.isNotBlank(title)){
                return title;
            }
        }
        return null;
    }

    @Override
    public String getDescription(Document document) {
        for (UrlDiscover discover : urlDiscoverList) {
            String description = discover.getDescription(document);
            if(StrUtil.isNotBlank(description)){
                return description;
            }
        }
        return null;
    }

    @Override
    public String getImage(String url, Document document) {
        for (UrlDiscover discover : urlDiscoverList) {
            String image = discover.getImage(url, document);
            if(StrUtil.isNotBlank(image)){
                return image;
            }
        }
        return null;
    }
}
