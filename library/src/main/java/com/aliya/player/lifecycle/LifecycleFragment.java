package com.aliya.player.lifecycle;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 视频生命周期管理 Fragment
 *
 * @author a_liYa
 * @date 2017/8/16 20:49.
 */
public class LifecycleFragment extends Fragment {

    private LifecycleListener mLifecycle;
    // 是否已经异步删除
    private boolean isAsyncRemove;

    public LifecycleListener getLifecycleListener() {
        return mLifecycle;
    }

    public void setLifecycleListener(LifecycleListener lifecycleListener) {
        mLifecycle = lifecycleListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAsyncRemove = false;
        Log.e("TAG", "onCreate " + this);
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

    /**
     * 标记已经异步删除
     */
    public void tagAsyncRemove() {
        mLifecycle = null;
        isAsyncRemove = true;
        Log.e("TAG", "tagAsyncRemove " + this);
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