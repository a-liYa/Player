package com.aliya.player.lifecycle;

/**
 * 视频生命周期监听 - 接口
 *
 * @author a_liYa
 */
public interface LifecycleListener {

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onHiddenChanged(boolean hidden);

}
