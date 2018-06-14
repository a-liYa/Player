package com.aliya.player.ui.control;

import android.content.Context;
import android.view.View;

import com.aliya.player.Control;
import com.aliya.player.ui.Controller;
import com.aliya.player.ui.PlayerView;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * AbsControl is Control abstract implementation
 *
 * @author a_liYa
 * @date 2017/8/13 16:45.
 */
public abstract class AbsControl implements Control {

    protected Controller controller;
    protected VisibilityListener visibilityListener;

    public AbsControl(Controller controller) {
        this.controller = controller;
    }

    @Override
    public SimpleExoPlayer getPlayer() {
        return controller != null ? controller.getPlayer() : null;
    }

    @Override
    public PlayerView getPlayerView() {
        return controller != null ? controller.getPlayerView() : null;
    }

    @Override
    public void onControl(int action) {
    }

    @Override
    public void syncRegime(Control control) {
    }

    @Override
    public View getParentView() {
        PlayerView playerView = getPlayerView();
        if (playerView != null) {
            return (View) playerView.getParent();
        }
        return null;
    }

    @Override
    public VisibilityListener getVisibilityListener() {
        return visibilityListener;
    }

    @Override
    public void setVisibilityListener(VisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }

    @Override
    public Context getContext(){
        return controller != null ? controller.getContext() : null;
    }

}
