package com.snwolf.chat.common.common.service.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Iterables;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/5/2024
 * @Description: 使用本地缓存Caffeine实现批量缓存框架
 * @note: 这里所有的参数key/keyList, 就是本地缓存Caffeine中缓存对象的key
 * <p>因为Caffeine本来就是对本地缓存的一层封装, 因此在实现批量缓存框架的时候, 就较为简单
 */
public abstract class AbstractBatchCaffeineCache<R, T> implements BatchCache<R, T>{

    public static final int DEFAULT_REFRESH_SECONDS = 60;
    public static final int DEFAULT_EXPIRE_SECONDS = 10 * 60;
    public static final int DEFAULT_MAX_SIZE = 1024;
    private Class<R> outClass;

    /**
     * @note: 因为在实现loadAll方法的时候, 需要将Iterable类型的T转换为{@code List<T>}, 在使用工具类转换的时候, 需要用到T对应的类型
     * <p>因此这里获取一下
     */
    private Class<T> inClass;
    private LoadingCache<T, R> cache;

    protected AbstractBatchCaffeineCache(){
        init(DEFAULT_REFRESH_SECONDS, DEFAULT_EXPIRE_SECONDS, DEFAULT_MAX_SIZE);
    }

    protected AbstractBatchCaffeineCache(long refreshSeconds, long expireSeconds, int maxSize){
        init(refreshSeconds, expireSeconds, maxSize);
    }

    protected abstract Map<T, R> load(List<T> keyList);


    private void init(long refreshSeconds, long expireSeconds, int maxSize){
        // 获取T和R的具体类型
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<R>) genericSuperclass.getActualTypeArguments()[0];
        this.inClass = (Class<T>) genericSuperclass.getActualTypeArguments()[1];
        cache = Caffeine.newBuilder()
                .refreshAfterWrite(refreshSeconds, TimeUnit.SECONDS)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build(new CacheLoader<T, R>() {
                    @Override
                    public @Nullable R load(@NonNull T key) throws Exception {
                        return AbstractBatchCaffeineCache.this.load(Collections.singletonList(key)).get(key);
                    }

                    @Override
                    public @NonNull Map<@NonNull T, @NonNull R> loadAll(@NonNull Iterable<? extends @NonNull T> keys) throws Exception {
                        List<T> keysList = Arrays.asList(Iterables.toArray(keys, inClass));
                        return AbstractBatchCaffeineCache.this.load(keysList);
                    }
                });
    }

    @Override
    public R get(T key) {
        return cache.get(key);
    }

    @Override
    public Map<T, R> getBatch(List<T> keyList) {
        return cache.getAll(keyList);
    }

    @Override
    public void delete(T key) {
        cache.invalidate(key);
    }

    @Override
    public void deleteBatch(List<T> keyList) {
        cache.invalidateAll(keyList);
    }
}
