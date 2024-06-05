package com.snwolf.chat.common.common.service.cache;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description: 批量缓存框架接口
 * @param <T> : 缓存Key类型
 * @param <R> : 缓存Value类型
 */
public interface BatchCache<R, T> {

    /**
     * 获取单个
     * @param key: 缓存key
     * @return: 缓存value
     */
    R get(T key);

    /**
     * 批量获取
     * @param keyList: 缓存key的列表
     * @return: <key, value> 缓存key和value的对应关系集合
     */
    Map<T, R> getBatch(List<T> keyList);

    /**
     * 删除单个
     * @param key: 缓存key
     */
    void delete(T key);

    /**
     * 删除多个
     * @param keyList: 要删除的缓存key列表
     */
    void deleteBatch(List<T> keyList);
}
