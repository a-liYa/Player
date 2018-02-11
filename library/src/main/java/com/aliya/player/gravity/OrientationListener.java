package com.aliya.player.gravity;

import android.content.pm.ActivityInfo;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 重力感应方向 - 监听
 *
 * @author a_liYa
 * @date 2018/2/11 10:44.
 */
public interface OrientationListener {

    /**
     * 屏幕取向回调
     *
     * @param orientation 取向：横屏、竖屏、横屏翻转、竖屏翻转
     */
    void onOrientation(@ScreenOrientation int orientation);

    @IntDef(value = {
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,  // 横屏翻转
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,   // 竖屏翻转
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,          // 横屏
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT            // 竖屏
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScreenOrientation {
    }

}
