package com.aliya.player;

import com.aliya.player.ui.PlayerView;

/**
 * player all callback interface
 *
 * @author a_liYa
 * @date 2017/11/21 19:14.
 */
public interface PlayerCallback {

    /**
     * 暂停 call back
     *
     * @param view current player view
     */
    void onPause(PlayerView view);

    /**
     * 播放 call back
     *
     * @param view current player view
     */
    void onPlay(PlayerView view);

    /**
     * 重播 Callback
     *
     * @param view current player view
     */
    void onReplay(PlayerView view);

    /**
     * 全屏／取消全屏 call back
     *
     * @param isFullscreen true : 切到全屏; false : 取消全屏
     * @param view         current player view
     */
    void onFullscreenChange(boolean isFullscreen, PlayerView view);

    /**
     * 静音／取消静音 call back
     *
     * @param isMute true : 设置静音; false : 取消静音
     * @param view   current player view
     */
    void onMuteChange(boolean isMute, PlayerView view);

}
