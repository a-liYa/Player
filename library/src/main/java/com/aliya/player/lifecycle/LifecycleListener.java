package com.aliya.player.lifecycle;

import android.support.annotation.NonNull;

/**
 * 视频生命周期监听 - 接口
 *
 * @author a_liYa
 */
public interface LifecycleListener {

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onDetach();

    boolean isAtLeast(@NonNull LifecycleState state);

    enum LifecycleState {
        // [onDestroy, +)
        DESTROYED,

        // (+, onCreate)
        INITIALIZED,

        // [onCreate, onDestroy)
        CREATED,

        // [onStart, onStop)
        STARTED,

        // [onResume, onPause)
        RESUMED;

        public boolean isAtLeast(@NonNull LifecycleState state) {
            return compareTo(state) >= 0;
        }
    }

}
