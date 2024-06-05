package com.snwolf.chat.common.common.service.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.snwolf.chat.common.common.utils.RedisUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description: 使用Redis实现的批量缓存框架抽象工具类
 * @note: 所有的参数key, keyList都不是真正的缓存key, 而是缓存key的一部分, 需要调用子类重写的{@code getKey(T key)}方法获得真正的缓存key
 */
public abstract class AbstractBatchStringRedisCache<R, T> implements BatchCache<R, T> {

    /**
     * 在本类的构造函数中, 从类的泛型参数中获取缓存value的类型, 保存到成员变量的位置
     */
    private Class<R> outClass;

    protected AbstractBatchStringRedisCache() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<R>) genericSuperclass.getActualTypeArguments()[0];
    }

    /**
     * 通过缓存key中的参数key, 获取真正的缓存key
     *
     * @param key
     * @return
     */
    protected abstract String getKey(T key);

    /**
     * 获取缓存key的过期时间
     *
     * @return
     */
    protected abstract Long getExpireSeconds();

    /**
     * 通过key集合, 从数据库中批量查询数据的方法
     *
     * @param keyList: 参数集合, 比如{@code List<uid>}
     * @return
     */
    protected abstract Map<T, R> load(List<T> keyList);


    /**
     *
     * @param key: 缓存key中的参数, 需要调用子类重写的getKey(T key)方法获得真正的缓存key
     * @return
     */
    @Override
    public R get(T key) {
        Map<T, R> single = getBatch(Arrays.asList(key));
        return single.get(key);
    }

    /**
     *
     * @param keyList: 缓存key中的参数列表, 需要调用子类重写的getKey(T key)方法获得真正的缓存key
     * @return
     */
    @Override
    public Map<T, R> getBatch(List<T> keyList) {
        // 从缓存中批量获取
        List<String> redisKeyList = keyList.stream()
                .map(this::getKey)
                .collect(Collectors.toList());
        List<R> redisResultList = RedisUtils.mget(redisKeyList, outClass);
        // 找出缓存中缺少的数据
        Map<T, R> redisResultMap = new HashMap<>();
        List<T> needRefreshList = new ArrayList<>();
        for (int i = 0; i < redisResultList.size(); i++) {
            if (ObjectUtil.isNull(redisResultList.get(i))) {
                needRefreshList.add(keyList.get(i));
            } else {
                redisResultMap.put(keyList.get(i), redisResultList.get(i));
            }
        }
        // 从数据库中批量获取缓存中缺少的数据
        if (CollectionUtil.isNotEmpty(needRefreshList)) {
            Map<T, R> dbResultMap = load(needRefreshList);
            if (CollectionUtil.isNotEmpty(dbResultMap)) {
                // 批量设置缓存
                Map<String, R> dbResultStringMap = dbResultMap.entrySet().stream()
                        .collect(Collectors.toMap(entry -> getKey(entry.getKey()), Map.Entry::getValue));
                RedisUtils.mset(dbResultStringMap, getExpireSeconds());
                // 添加数据到结果集
                redisResultMap.putAll(dbResultMap);
            }
        }
        return redisResultMap;
    }

    /**
     *
     * @param key: 缓存key中的参数, 需要调用子类重写的getKey(T key) 方法获得真正的缓存key
     */
    @Override
    public void delete(T key) {
        deleteBatch(Arrays.asList(key));
    }

    /**
     *
     * @param keyList: 缓存key中的参数列表, 需要调用子类重写的getKey(T key) 方法获得真正的缓存key
     */
    @Override
    public void deleteBatch(List<T> keyList) {
        List<String> redisKeyList = keyList.stream()
                .map(this::getKey)
                .collect(Collectors.toList());
        RedisUtils.del(redisKeyList);
    }
}
