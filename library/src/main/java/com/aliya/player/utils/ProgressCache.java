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

    public LruCache<String, Integer> lruCaches; // 最近最少（Least Recently Used）

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

    public void put(String key, int position) {
        if (key != null && position > 0) {
            lruCaches.put(key, Integer.valueOf(position));
        }
    }

    public int getCacheProgress(String key) {
        Integer value = lruCaches.get(key);
        if (value != null) {
            return value.intValue();
        }
        return NO_VALUE;
    }

}
