package com.aliya.player.ui;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliya.player.R;
import com.aliya.player.utils.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.aliya.player.utils.Utils.findViewById;
import static com.aliya.player.utils.Utils.setText;

/**
 * NavBarControl
 *
 * @author a_liYa
 * @date 2017/8/11 21:29.
 */
public class NavBarControl extends AbsControl {

    private ImageView ivPause;

    private SeekBar seekBar;

    private TextView tvPosition;

    private TextView tvDuration;

    private ImageView ivFullscreen;

    private View rootView;

    private CalcTime mCalcTime;
    private int showTimeoutMs;
    private long hideAtMs;
    private boolean isAttachedToWindow;

    private ComponentListener componentListener;

    public static final int DEFAULT_SHOW_TIMEOUT_MS = 3000;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            setVisibility(false);
        }
    };

    public NavBarControl(Controller controller) {
        super(controller);
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        mCalcTime = new CalcTime();
        componentListener = new ComponentListener();
    }

    public void onViewCreate(View view) {

        if (rootView != null) {
            rootView.removeOnAttachStateChangeListener(componentListener);
        }

        rootView = view;

        rootView.addOnAttachStateChangeListener(componentListener);

        ivPause = findViewById(rootView, R.id.player_play_pause);
        seekBar = findViewById(rootView, R.id.player_seek_bar);
        tvPosition = findViewById(rootView, R.id.player_position);
        tvDuration = findViewById(rootView, R.id.player_duration);
        ivFullscreen = findViewById(rootView, R.id.player_full_screen);

        if (ivPause != null) {
            ivPause.setOnClickListener(componentListener);
        }
        if (ivFullscreen != null) {
            ivFullscreen.setOnClickListener(componentListener);
        }
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(componentListener);
        }

    }

    public void stopUpdateProgress() {
        if (rootView != null) {
            rootView.removeCallbacks(updateProgressAction);
        }
    }

    public void updateProgress() {

        Player player = getPlayer();
        if (player == null) return;

        mCalcTime.calcTime(player);

        if (tvPosition != null && !componentListener.seekBarIsDragging) {
            setText(tvPosition, Utils.formatTime(mCalcTime.position));
        }
        if (tvDuration != null) {
            setText(tvDuration, Utils.formatTime(mCalcTime.duration));
        }

        if (seekBar != null && seekBar.getVisibility() == VISIBLE) {
            if (mCalcTime.duration > 0) {
                int progress = (int)
                        (seekBar.getMax() * mCalcTime.position / mCalcTime.duration + 0.5f);

                if (!componentListener.seekBarIsDragging) {
                    if (progress > seekBar.getMax()) {
                        progress = seekBar.getMax();
                    }
                    seekBar.setProgress(progress);
                }

                int bufferProgress = (int)
                        (seekBar.getMax() * mCalcTime.bufferedPosition / mCalcTime.duration + 0.5f);
                if (bufferProgress > seekBar.getMax()) {
                    bufferProgress = seekBar.getMax();
                }
                seekBar.setSecondaryProgress(bufferProgress);
            }
        }

        // Cancel any pending updates and schedule a new one if necessary.
        stopUpdateProgress();

        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = Utils.calcSyncPeriod(mCalcTime.position);
            } else {
                delayMs = 1000;
            }
            rootView.postDelayed(updateProgressAction, delayMs);
        }

    }

    public void updatePlayPause(boolean playWhenReady) {
        if (ivPause != null) {
            ivPause.setImageResource(playWhenReady
                    ? R.mipmap.module_player_controls_pause : R.mipmap.module_player_controls_play);
        }
    }

    public void hideAfterTimeout() {
        rootView.removeCallbacks(hideAction);

        if (getPlayer() != null && !getPlayer().getPlayWhenReady()) {
            hideAtMs = C.TIME_UNSET;
        } else if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow) {
                rootView.postDelayed(hideAction, showTimeoutMs);
            }
        } else {
            hideAtMs = C.TIME_UNSET;
        }
    }

    @Override
    public void setVisibility(boolean isVisible) {
        if (rootView != null) {
            boolean oldVisible = isVisible();
            rootView.setVisibility(isVisible ? VISIBLE : GONE);
            if (oldVisible != isVisible) {
                if (visibilityListener != null) {
                    visibilityListener.onVisibilityChange(this, isVisible);
                }
            }
            if (isVisible) {
                updateProgress();
                hideAfterTimeout();
            } else {
                rootView.removeCallbacks(hideAction);
                stopUpdateProgress();
                hideAtMs = C.TIME_UNSET;
            }
        }
    }

    /**
     * 切换可见性 - 显示／隐藏
     *
     * @return true表示切换为显示; false表示切换为隐藏
     */
    public boolean switchVisibility() {
        if (rootView == null) return false;

        setVisibility(!isVisible());

        return isVisible();
    }

    @Override
    public boolean isVisible() {
        return rootView.getVisibility() == VISIBLE;
    }

    private final class ComponentListener implements View.OnClickListener,
            SeekBar.OnSeekBarChangeListener, View.OnAttachStateChangeListener {

        private boolean seekBarIsDragging;

        @Override
        public void onClick(View v) {
            Player player = getPlayer();
            if (player != null) {
                if (v.getId() == R.id.player_play_pause) {
                    player.setPlayWhenReady(!player.getPlayWhenReady());
                } else if (v.getId() == R.id.player_full_screen) {
                    Log.e("TAG", "player_full_screen");
                }
            }
            hideAfterTimeout();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mCalcTime != null && tvPosition != null) {
                setText(tvPosition,
                        Utils.formatTime(mCalcTime.duration * progress / seekBar.getMax()));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            seekBarIsDragging = true;
            rootView.removeCallbacks(hideAction);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBarIsDragging = false;
            if (controller != null) {
                controller.seekTo(mCalcTime.duration * seekBar.getProgress() / seekBar.getMax());
            }
            hideAfterTimeout();
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            isAttachedToWindow = true;
            if (hideAtMs != C.TIME_UNSET) {
                long delayMs = hideAtMs - SystemClock.uptimeMillis();
                if (delayMs <= 0) {
                    setVisibility(false);
                } else {
                    rootView.postDelayed(hideAction, delayMs);
                }
            }
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            isAttachedToWindow = false;
            rootView.removeCallbacks(hideAction);
        }
    }

}
