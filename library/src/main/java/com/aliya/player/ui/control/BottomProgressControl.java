package com.aliya.player.ui.control;

import android.view.View;
import android.widget.ProgressBar;

import com.aliya.player.ui.Controller;

/**
 * 底部进度 - Control
 *
 * @author a_liYa
 * @date 2017/9/17 16:48.
 */
public class BottomProgressControl extends AbsControl {

    private ProgressBar mProgressBar;

    public BottomProgressControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ProgressBar) {
            mProgressBar = (ProgressBar) view;
        }
    }

    @Override
    public boolean isVisible() {
        return mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setVisibility(boolean isVisible) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            updateProgress();
        }
    }

    public void updateProgress() {
        if (mProgressBar == null) return;

        CalcTime calcTime = controller.getCalcTime();

        int progress = calcTime.calcProgress(mProgressBar.getMax());

        if (progress > mProgressBar.getMax()) {
            progress = mProgressBar.getMax();
        }
        mProgressBar.setProgress(progress);

        int secondaryProgress = calcTime.calcSecondaryProgress(mProgressBar.getMax());

        if (secondaryProgress > mProgressBar.getMax()) {
            secondaryProgress = mProgressBar.getMax();
        }
        mProgressBar.setSecondaryProgress(secondaryProgress);
    }

}
