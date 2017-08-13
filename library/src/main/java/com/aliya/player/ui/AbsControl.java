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

    public AbsControl(Controller controller) {
        this.controller = controller;
    }

}
