package com.aliya.player.utils;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 视频工具类
 *
 * @author a_liYa
 * @date 2017/5/9 10:54.
 */
public class VideoUtils {

    /**
     * 计算同步周期时长 预防进度1s更新延迟问题
     *
     * @param position position
     * @return delayMs
     */
    public static long calcSyncPeriod(long position) {
        long delayMs = 1000 - (position % 1000);
        return delayMs < 200 ? delayMs + 200 : delayMs;
    }

    /**
     * 下载速度格式化显示
     *
     * @param speed 网速 单位：Byte/s
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
     */
    public static String formatTime(long time) {
        if (time < 0) {
            return "--:--";
        }
        int totalSeconds = (int) (time / 1000);
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
     */
    public static String formatSize(int size) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2); // 最多保留两位小数
        numberFormat.setRoundingMode(RoundingMode.HALF_UP); // 四舍五入
        return numberFormat.format(size / (1024 * 1024f));
    }

    /**
     * dp转换px
     */
    public static int dp2px(Context context, float dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * 当前播放的是否是直播
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

    public final static <T extends View> T findViewById(View parent, @IdRes int id) {
        if (parent == null) return null;

        View findView = parent.findViewById(id);
        if (findView != null) {
            return (T) findView;
        }

        return null;
    }

}
