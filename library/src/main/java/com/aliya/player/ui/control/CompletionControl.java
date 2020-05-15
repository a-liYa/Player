package com.aliya.player.ui.control;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.aliya.player.PlayerCallback;
import com.aliya.player.PlayerManager;
import com.aliya.player.R;
import com.aliya.player.ui.Controller;
import com.aliya.player.ui.PlayerView;
import com.aliya.player.utils.Utils;

/**
 * CompletionControl
 *
 * @author a_liYa
 * @date 2017/8/13 11:19.
 */
public class CompletionControl extends AbsControl {

    private ViewStub viewStub;
    private View rootView;
    private TextView tvHint;

    public CompletionControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ViewStub) {
            viewStub = (ViewStub) view;
            viewStub.setLayoutResource(R.layout.module_player_layout_completion);
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
                        // 回调播放完毕点击重播
                        PlayerCallback callback = PlayerManager.getPlayerCallback(getParentView());
                        if (callback != null) {
                            callback.onReplay(playerView);
                        }
                    }
                } else {
                    Toast.makeText(view.getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
