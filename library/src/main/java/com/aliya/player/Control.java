package com.aliya.player;

import android.view.View;

import com.aliya.player.ui.PlayerView;
import com.google.android.exoplayer2.Player;

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

    Player getPlayer();

    PlayerView getPlayerView();

    void setVisibilityListener(VisibilityListener visibilityListener);

    VisibilityListener getVisibilityListener();

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param control   .
         * @param isVisible .
         */
        void onVisibilityChange(Control control, boolean isVisible);

    }

}
