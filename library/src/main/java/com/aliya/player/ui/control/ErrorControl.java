package com.aliya.player.ui.control;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.aliya.player.R;
import com.aliya.player.ui.Controller;
import com.aliya.player.ui.PlayerView;
import com.aliya.player.utils.Utils;

/**
 * ErrorControl
 *
 * @author a_liYa
 * @date 2017/8/13 11:19.
 */
public class ErrorControl extends AbsControl {

    private ViewStub viewStub;
    private View rootView;
    private TextView tvHint;

    public ErrorControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ViewStub) {
            viewStub = (ViewStub) view;
            viewStub.setLayoutResource(R.layout.module_player_layout_error);
        }
    }

    @Override
    public boolean isVisible() {
        return rootView != null && rootView.getVisibility() == View.VISIBLE ? true : false;
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

    /**
     * 显示播放完毕重播
     */
    public void showPlayEnded() {
        setVisibility(true);
        if (tvHint != null) {
            tvHint.setText("重播");
        }
    }

    private void show() {
        if (rootView == null) {
            if (viewStub != null) {
                rootView = viewStub.inflate();
                viewStub = null;
                tvHint = (TextView) rootView.findViewById(R.id.player_tv_hint);
                View view = rootView.findViewById(R.id.player_click_retry);
                view.setOnClickListener(mOnClickListener);
            }
        }
        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        if (rootView != null) {
            rootView.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.player_click_retry) {
                if (Utils.isAvailable(view.getContext()) && controller != null) {
                    setVisibility(false);
                    tvHint.setText("播放失败");
                    PlayerView playerView = controller.getPlayerView();
                    if (playerView != null) {
                        playerView.replay();
                    }
                } else {
                    Toast.makeText(view.getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
