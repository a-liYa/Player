package com.aliya.player.ui.control;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.aliya.player.Extra;
import com.aliya.player.R;
import com.aliya.player.ui.Controller;
import com.aliya.player.utils.Recorder;
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
        // 以及阻塞提醒，直接toast提醒就行
        if (isVisible && Recorder.get().isAllowMobileTraffic(Extra.getExtraUrl(getPlayerView()))) {
            Context context = getContext();
            if (context != null) {
                Toast toast = Toast.makeText(context, R.string.player_mobile_traffic_alert,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            return; // 直接return
        }

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
            Recorder.get().allowMobileTraffic(Extra.getExtraUrl(getPlayerView()));
            SimpleExoPlayer player = getPlayer();
            if (player != null) {
                player.setPlayWhenReady(true);
            }
            setVisibility(false);
        }
    }

}
