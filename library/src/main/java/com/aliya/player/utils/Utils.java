package com.aliya.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.aliya.player.Control;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 视频工具类
 *
 * @author a_liYa
 * @date 2017/5/9 10:54.
 */
public class Utils {

    /**
     * 下载速度格式化显示
     *
     * @param speed 网速 单位：Byte/s
     * @return 格式化
     */
    public static String formatSpeed(int speed) {
        long fileSize = (long) speed;
        String showSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            showSize = fileSize + "B/s";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            showSize = Long.toString(fileSize / 1024) + "KB/s";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            showSize = Long.toString(fileSize / (1024 * 1024)) + "MB/s";
        }
        return showSize;
    }

    /**
     * 时长格式化显示
     *
     * @param timeMs 时长 单位：ms
     * @return 格式化
     */
    public static String formatTime(long timeMs) {
        if (timeMs < 0) {
            return "--:--";
        }
        int totalSeconds = (int) ((timeMs + 500) / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String
                .format("%02d:%02d", minutes, seconds);
    }

    /**
     * 视频大小格式化显示
     *
     * @param size 大小 单位：Byte
     * @return 格式化
     */
    public static String formatSize(int size) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2); // 最多保留两位小数
        numberFormat.setRoundingMode(RoundingMode.HALF_UP); // 四舍五入
        return numberFormat.format(size / (1024 * 1024f));
    }

    /**
     * dp转换px
     *
     * @param context 上下文
     * @param dip     dp
     * @return px
     */
    public static int dp2px(Context context, float dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * 当前播放的是否是直播
     *
     * @param url .
     * @return true 表示直播类型
     */
    public static boolean isLive(String url) {
        if (url != null
                && (url.startsWith("rtmp://")
                || (url.startsWith("http://") && url.endsWith(".m3u8"))
                || (url.startsWith("https://") && url.endsWith(".m3u8"))
                || (url.startsWith("http://") && url.endsWith(".flv"))
                || (url.startsWith("https://") && url.endsWith(".flv")))) {
            return true;
        }
        return false;
    }

    /**
     * 网络是否可用
     *
     * @param context .
     * @return true 表示可用
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 是否为移动网络
     *
     * @param context .
     * @return true:移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return ConnectivityManager.TYPE_MOBILE == info.getType();
    }

    /**
     * 是否为WiFi网络
     *
     * @param context .
     * @return true：WiFi
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return ConnectivityManager.TYPE_WIFI == info.getType();
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    public final static <T extends View> T findViewById(View parent, @IdRes int id) {
        if (parent == null) return null;

        View findView = parent.findViewById(id);
        if (findView != null) {
            return (T) findView;
        }

        return null;
    }

    public static void setText(TextView tv, String text) {
        if (tv == null) return;
        if (!TextUtils.equals(tv.getText(), text)) {
            tv.setText(text);
        }
    }

    public static void setVisibilityControls(boolean isVisible, Control... controls) {
        if (controls != null) {
            for (Control control : controls) {
                if (control != null)
                    control.setVisibility(isVisible);
            }
        }
    }

    /**
     * 通过Context获取所属Activity
     *
     * @param context Context
     * @return activity
     */
    public static @Nullable Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

}
