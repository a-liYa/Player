package com.aliya.player.ui;

import com.aliya.player.Control;

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

    public VisibilityListener getVisibilityListener() {
        return visibilityListener;
    }

    public void setVisibilityListener(VisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }
}
