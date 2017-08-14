package com.aliya.player.utils;

import android.util.LruCache;

/**
 * 进度缓存
 *
 * @author a_liYa
 * @date 2017/8/13 21:04.
 */
public class ProgressCache {

    private static final int DEFAULT_PROGRESS_CACHE_MAX_SIZE = 2; // 默认缓存 max size
    public static final int NO_VALUE = 0;

    private LruCache<String, Long> lruCaches; // 最近最少（Least Recently Used）

    private static volatile ProgressCache sInstance;

    private ProgressCache(int maxSize) {
        this.lruCaches = new LruCache<>(maxSize);
    }

    public static ProgressCache get() {
        if (sInstance == null) {
            init(DEFAULT_PROGRESS_CACHE_MAX_SIZE);
        }
        return sInstance;
    }

    public static void init(int cacheMaxSize) {
        if (sInstance == null) {
            synchronized (ProgressCache.class) {
                if (sInstance == null) {
                    sInstance = new ProgressCache(cacheMaxSize);
                }
            }
        }
    }

    public void putCacheProgress(String key, long position) {
        if (key != null && position > 0) {
            lruCaches.put(key, Long.valueOf(position));
        }
    }

    public void removeCacheProgress(String key) {
        if (key != null) {
            lruCaches.remove(key);
        }
    }

    public int getCacheProgress(String key) {
        Long value = lruCaches.get(key);
        if (value != null) {
            return value.intValue();
        }
        return NO_VALUE;
    }

}
