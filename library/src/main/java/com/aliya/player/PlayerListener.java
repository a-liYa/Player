package com.aliya.player;

import android.view.View;

/**
 * player listener
 *
 * @author a_liYa
 * @date 2017/8/16 09:56.
 */
public interface PlayerListener extends View.OnAttachStateChangeListener {

    /**
     * 全屏切换 callback
     *
     * @param isFullscreen true : 全屏
     */
    void onChangeFullScreen(boolean isFullscreen);

    /**
     * 播放结束
     */
    void playEnded();

}
