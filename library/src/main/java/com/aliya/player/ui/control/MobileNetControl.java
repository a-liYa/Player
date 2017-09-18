package com.aliya.player.ui.control;

import android.view.View;
import android.view.ViewStub;

import com.aliya.player.R;
import com.aliya.player.ui.Controller;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * 移动网络提醒 - control
 *
 * @author a_liYa
 * @date 2017/9/18 17:32.
 */
public class MobileNetControl extends AbsControl implements View.OnClickListener {

    private ViewStub viewStub;
    private View rootView;

    public MobileNetControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ViewStub) {
            viewStub = (ViewStub) view;
            viewStub.setLayoutResource(R.layout.module_player_layout_mobile_network);
        }
    }

    @Override
    public boolean isVisible() {
        return rootView == null ? false : rootView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setVisibility(boolean isVisible) {
        boolean oldVisible = isVisible();
        if (isVisible) {
            show();
        } else {
            hide();
        }
        if (oldVisible != isVisible) {
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(this, isVisible);
            }
        }
    }

    private void show() {
        if (rootView == null) {
            if (viewStub != null) {
                rootView = viewStub.inflate();
                viewStub = null;
                View view = rootView.findViewById(R.id.player_click_retry);
                view.setOnClickListener(this);
            }
        }
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
        }
        SimpleExoPlayer player = getPlayer();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void hide() {
        if (rootView != null) {
            rootView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.player_click_retry) { // 播放
            SimpleExoPlayer player = getPlayer();
            if (player != null) {
                player.setPlayWhenReady(true);
            }
            setVisibility(false);
        }
    }

}
