package com.aliya.player;

import android.support.annotation.NonNull;

import com.aliya.player.lifecycle.LifecycleListener;
import com.aliya.player.ui.PlayerView;

import java.lang.ref.SoftReference;

/**
 * Player的生命周期实现类
 *
 * @author a_liYa
 */
public class PlayerLifecycleImpl implements LifecycleListener {

    private SoftReference<PlayerView> playerViewSoft;
    private boolean lifecycleFollowFlag = true; // true:表示跟随生命周期
    LifecycleState mState = LifecycleState.INITIALIZED;

    public PlayerLifecycleImpl(PlayerView playerManager) {
        playerViewSoft = new SoftReference<>(playerManager);
    }

    @Override
    public void onCreate() {
        mState = LifecycleState.CREATED;
    }

    @Override
    public void onStart() {
        mState = LifecycleState.STARTED;
    }

    @Override
    public void onResume() {
        mState = LifecycleState.RESUMED;
    }

    @Override
    public void onPause() {
        mState = LifecycleState.STARTED;
        stopPlayer();
    }

    @Override
    public void onStop() {
        mState = LifecycleState.CREATED;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopPlayer();
        }
    }

    @Override
    public void onDestroy() {
        mState = LifecycleState.DESTROYED;
    }

    @Override
    public void onDetach() {
        mState = LifecycleState.INITIALIZED;
    }

    @Override
    public boolean isAtLeast(@NonNull LifecycleState state) {
        return mState.isAtLeast(state);
    }

    public boolean isLifecycleFollowFlag() {
        return lifecycleFollowFlag;
    }

    public void setLifecycleFollowFlag(boolean lifecycleFollowFlag) {
        this.lifecycleFollowFlag = lifecycleFollowFlag;
    }

    private void stopPlayer() {
        if (lifecycleFollowFlag) {
            if (playerViewSoft != null && playerViewSoft.get() != null) {
                playerViewSoft.get().release();
            }
        }
    }

}
