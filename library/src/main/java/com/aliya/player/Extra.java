package com.aliya.player;

import android.view.View;

/**
 * 额外信息
 *
 * @author a_liYa
 * @date 2017/8/14 16:05.
 */
public class Extra {

    private String url;

    private Object data;

    private Extra(String url, Object data) {
        this.url = url;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public Object getData() {
        return data;
    }

    public static void setExtra(View v, String url, Object data) {
        if (v != null) {
            v.setTag(R.id.player_tag_extra, new Extra(url, data));
        }
    }

    public static String getExtraUrl(View v) {
        if (v != null) {
            Object tag = v.getTag(R.id.player_tag_extra);
            if (tag instanceof Extra) {
                return ((Extra) tag).getUrl();
            }
        }
        return null;
    }

    public static <T> T getExtraData(View v) {
        if (v != null) {
            Object tag = v.getTag(R.id.player_tag_extra);
            if (tag instanceof Extra) {
                Object data = ((Extra) tag).getData();
                if (data != null)
                    return (T) data;
            }
        }
        return null;
    }

}
