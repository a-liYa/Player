package com.aliya.player.ui.control;

import android.view.View;
import android.widget.ImageView;

import com.aliya.player.PlayerCallback;
import com.aliya.player.PlayerManager;
import com.aliya.player.R;
import com.aliya.player.ui.Controller;

/**
 * 静音 - 控制器
 *
 * @author a_liYa
 * @date 2017/9/18 14:36.
 */
public class MuteControl extends AbsControl implements View.OnClickListener {

    ImageView iv;

    protected static boolean isMute = false;

    public MuteControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ImageView) {
            iv = (ImageView) view;
            iv.setOnClickListener(this);
            updateVolume();
        }
    }

    @Override
    public boolean isVisible() {
        return iv == null ? false : iv.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setVisibility(boolean isVisible) {
        if (iv != null) {
            iv.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == iv) {
            if (getPlayer() != null) {
                isMute = !isMute;
                updateVolume();
                PlayerCallback callback = PlayerManager.getPlayerCallback(getParentView());
                if (callback != null) {
                    callback.onMuteChange(isMute, getPlayerView());
                }
            }
        }
    }

    public void updateVolume() {
        if (getPlayer() != null) {
            getPlayer().setVolume(isMute ? 0f : 1f);
        }
        iv.setImageResource(isMute ? R.mipmap.module_player_controls_ic_mute
                : R.mipmap.module_player_controls_ic_volume);
    }
}
