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
    private boolean mLifecycleListenerFlag = true;

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

    private void stopPlayer() {
        if (mLifecycleListenerFlag) {
            if (playerViewSoft != null && playerViewSoft.get() != null) {
                playerViewSoft.get().release();
            }
        }
    }

}
