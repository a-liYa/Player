package com.aliya.player;

import android.util.Log;

import com.aliya.player.lifecycle.LifecycleListener;

import java.lang.ref.SoftReference;

/**
 * Player的生命周期实现类
 *
 * @author a_liYa
 */
public class PlayerLifecycleImpl implements LifecycleListener {


    private SoftReference<PlayerManager> mPlayerSoft;
    private boolean mLifecycleListenerFlag = true;

    public PlayerLifecycleImpl(PlayerManager playerManager) {
        mPlayerSoft = new SoftReference<>(playerManager);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        if (mLifecycleListenerFlag) {
            stopPlayer();
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden && mLifecycleListenerFlag) {
            stopPlayer();
        }
    }

    private void stopPlayer() {
        if (mPlayerSoft != null && mPlayerSoft.get() != null) {
            PlayerManager manager = mPlayerSoft.get();
//            manager.stop();
            Log.e("TAG", "stopPlayer ");
        }
    }

}
