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
 * @note: 所有的参数key都不是真正的缓存key, 而是缓存key的一部分, 需要调用子类重写的{@code getKey(T key)}方法获得真正的缓存key
 */
public interface BatchCache<R, T> {

    /**
     * 获取单个
     * @param key: 缓存key中的参数, 具体的缓存key需要调用子类重写的getKey(T key)方法获得
     * @return: 缓存value
     */
    R get(T key);

    /**
     * 批量获取
     * @param keyList: 缓存key中的参数列表, 具体的缓存key需要调用子类重写的getKey(T key)方法获得
     * @return: <key, value> 缓存key和value的对应关系集合
     */
    Map<T, R> getBatch(List<T> keyList);

    /**
     * 删除单个
     * @param key: 要删除的key中的参数, 具体的缓存key需要调用子类重写的getKey(T key)方法获得
     */
    void delete(T key);

    /**
     * 删除多个
     * @param keyList: 要删除的key中参数列表, 具体的缓存key需要调用子类重写的getKey(T key)方法获得
     */
    void deleteBatch(List<T> keyList);
}
