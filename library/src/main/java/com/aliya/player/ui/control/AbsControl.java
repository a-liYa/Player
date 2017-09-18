package com.aliya.player.ui.control;

import com.aliya.player.Control;
import com.aliya.player.ui.Controller;
import com.aliya.player.ui.PlayerView;
import com.google.android.exoplayer2.Player;

/**
 * AbsControl is Control abstract implementation
 *
 * @author a_liYa
 * @date 2017/8/13 16:45.
 */
abstract class AbsControl implements Control {

    protected Controller controller;
    protected VisibilityListener visibilityListener;

    public AbsControl(Controller controller) {
        this.controller = controller;
    }

    @Override
    public Player getPlayer() {
        return controller != null ? controller.getPlayer() : null;
    }

    @Override
    public PlayerView getPlayerView() {
        return controller != null ? controller.getPlayerView() : null;
    }

    @Override
    public VisibilityListener getVisibilityListener() {
        return visibilityListener;
    }

    @Override
    public void setVisibilityListener(VisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }
}
