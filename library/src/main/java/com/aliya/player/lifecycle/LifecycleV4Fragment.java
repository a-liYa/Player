package com.aliya.player.lifecycle;

import android.support.v4.app.Fragment;

/**
 * 视频生命周期管理 Fragment
 *
 * @author a_liYa
 * @date 2017/8/16 20:50.
 */
public class LifecycleV4Fragment extends Fragment {

    // 是否已经异步删除
    private boolean isAsyncRemove;

    private LifecycleListener mLifecycle;

    public LifecycleListener getLifecycleListener() {
        return mLifecycle;
    }

    public void setLifecycleListener(LifecycleListener lifecycleListener) {
        mLifecycle = lifecycleListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLifecycle != null) {
            mLifecycle.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLifecycle != null) {
            mLifecycle.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLifecycle != null) {
            mLifecycle.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLifecycle != null) {
            mLifecycle.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLifecycle = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mLifecycle != null) {
            mLifecycle.onHiddenChanged(hidden);
        }
    }

    /**
     * 标记已经异步删除
     */
    public void tagAsyncRemove() {
        mLifecycle = null;
        isAsyncRemove = true;
    }

    /**
     * 是否已经异步删除
     *
     * @return true：异步删除
     */
    public boolean isAsyncRemove() {
        return isAsyncRemove;
    }

}
