package com.aliya.player.ui;

import android.view.View;
import android.widget.ProgressBar;

/**
 * BufferControl
 *
 * @author a_liYa
 * @date 2017/8/13 16:58.
 */
public class BufferControl extends AbsControl {

    private ProgressBar bufferProgress;

    public BufferControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ProgressBar)
            bufferProgress = (ProgressBar) view;
    }

    @Override
    public boolean isVisible() {
        return bufferProgress != null && bufferProgress.getVisibility() == View.VISIBLE
                ? true : false;
    }

    @Override
    public void setVisibility(boolean isVisible) {
        if (bufferProgress != null) {
            if (isVisible) {
                bufferProgress.setVisibility(View.VISIBLE);
            } else {
                bufferProgress.setVisibility(View.INVISIBLE);
            }
        }
    }

}
