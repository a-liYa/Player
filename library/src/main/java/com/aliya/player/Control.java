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

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    interface VisibilityListener {

        /**
         * Called when the visibility changes.
         */
        void onVisibilityChange(Control control, boolean isVisible);

    }

}
