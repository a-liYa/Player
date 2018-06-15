package com.aliya.player.ui.control;

import android.view.View;

import com.aliya.player.ui.Controller;

/**
 * TitleControl
 *
 * @author a_liYa
 * @date 2018/6/15 14:32.
 */
public class TitleControl extends AbsControl {

    public TitleControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {

    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisibility(boolean isVisible) {

    }
}
