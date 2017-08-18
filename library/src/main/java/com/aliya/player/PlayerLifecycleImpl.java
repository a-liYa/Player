package com.aliya.player;

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

    public PlayerLifecycleImpl(PlayerView playerManager) {
        playerViewSoft = new SoftReference<>(playerManager);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        stopPlayer();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopPlayer();
        }
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
