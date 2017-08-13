package com.aliya.player;

import android.view.View;

/**
 * Control is player a modular interface，
 *
 * @author a_liYa
 * @date 2017/8/13 16:35.
 */
public interface Control {

    void onViewCreate(View view);

    boolean isVisible();

    void setVisibility(boolean isVisible);

}
