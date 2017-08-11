package com.aliya.player.simple;

/**
 * 测试数据
 *
 * @author a_liYa
 */
public class VideoUrls {

    private static String[] URLS = {
            "http://api.zjol.com.cn/api/video/140270?avaliable=hd",
            "http://api.zjol.com.cn/api/video/140270?avaliable=hd",
            "http://api.zjol.com.cn/api/video/140270?avaliable=hd",
            "http://cdn.v.zjol.com.cn/151999.mp4",
            "http://cdn.v.zjol.com.cn/151990.mp4",
            "http://cdn.v.zjol.com.cn/152038.mp4",
            "http://cdn.v.zjol.com.cn/152058.mp4",
    };

    public static String getTestUrl() {
        return URLS[0];
    }

    public static String getHttpsUrl() {
        return "https://v-cdn.zjol.com.cn/149064.mp4";
    }


    public static String[] getUrls() {
        return URLS;
    }

}
