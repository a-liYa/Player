package com.aliya.player.utils;

import android.util.LruCache;

/**
 * 记录数据 - 视频相关
 *
 * @author a_liYa
 * @date 2017/8/13 21:04.
 */
public class Recorder {

    public static final int NO_VALUE = 0;
    private static final int DEFAULT_CACHE_MAX_SIZE = 100; // 默认缓存 max size

    private static volatile Recorder sInstance;

    private Recorder(int maxSize) {
        this.lruProgress = new LruCache<>(maxSize);
        this.lruAllow = new LruCache<>(maxSize);
    }

    public static Recorder get() {
        if (sInstance == null) {
            init(DEFAULT_CACHE_MAX_SIZE);
        }
        return sInstance;
    }

    public static void init(int cacheMaxSize) {
        if (sInstance == null) {
            synchronized (Recorder.class) {
                if (sInstance == null) {
                    sInstance = new Recorder(cacheMaxSize);
                }
            }
        }
    }

    private LruCache<String, Long> lruProgress; // 最近最少（Least Recently Used）

    public void putCacheProgress(String key, long position) {
        if (key != null && position > 0) {
            lruProgress.put(key, Long.valueOf(position));
        }
    }

    public void removeCacheProgress(String key) {
        if (key != null) {
            lruProgress.remove(key);
        }
    }

    public int getCacheProgress(String key) {
        Long value = lruProgress.get(key);
        if (value != null) {
            return value.intValue();
        }
        return NO_VALUE;
    }

    private LruCache<String, Boolean> lruAllow;

    /**
     * 允许移动流量播放
     *
     * @param url a video url
     */
    public void allowMobileTraffic(String url) {
        if (url != null)
            lruAllow.put(url, Boolean.TRUE);
    }

    /**
     * 是否允许使用移动流量播放
     *
     * @param url a video url
     * @return true，已经允许使用移动流量播放
     */
    public boolean isAllowMobileTraffic(String url) {
        return url != null && lruAllow.get(url) == Boolean.TRUE;
    }

}
