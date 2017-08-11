package com.aliya.player;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliya.player.utils.VideoUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

/**
 * Controller
 *
 * @author a_liYa
 * @date 2017/8/11 13:05.
 */
public class Controller {

    private ProgressBar buffePrrogress;

    private View controlBar;

    private ImageView ivPause;

    private SeekBar seekBar;

    private TextView tvPosition;

    private TextView tvDuration;

    private ImageView ivFullscreen;

    private ViewGroup parentView;

    private Player player;

    private TimeTool timeTool;
    private final ComponentListener componentListener;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    public Controller(ViewGroup parentView) {
        this.parentView = parentView;
        timeTool = new TimeTool();
        componentListener = new ComponentListener();
    }

    public
    @LayoutRes
    int getLayoutRes() {
        return R.layout.module_player_layout_controller;
    }

    public void onViewCreate() {
        buffePrrogress = findViewById(R.id.player_buffer_progress);
        controlBar = findViewById(R.id.player_control_bar);
        ivPause = findViewById(R.id.player_play_pause);
        seekBar = findViewById(R.id.player_seek_bar);
        tvPosition = findViewById(R.id.player_position);
        tvDuration = findViewById(R.id.player_duration);
        ivFullscreen = findViewById(R.id.player_full_screen);

        if (ivPause != null) {
            ivPause.setOnClickListener(componentListener);
        }
        if (ivFullscreen != null) {
            ivFullscreen.setOnClickListener(componentListener);
        }

    }



    private void updateProgress() {

        timeTool.calcTime(player);

        if (tvPosition != null) {
            tvPosition.setText(VideoUtils.formatTime(timeTool.position));
        }
        if (tvDuration != null) {
            tvDuration.setText(VideoUtils.formatTime(timeTool.duration));
        }

        // Cancel any pending updates and schedule a new one if necessary.
        parentView.removeCallbacks(updateProgressAction);

        parentView.postDelayed(updateProgressAction, VideoUtils.calcSyncPeriod(0));
    }

    public void setPlayer(SimpleExoPlayer player) {
        if (this.player != player) {
            if (this.player != null) {
                this.player.removeListener(componentListener);
            }
            this.player = player;
            if (player != null) {
                player.addListener(componentListener);
            }
        }
    }

    private <T extends View> T findViewById(@IdRes int id) {
        if (parentView == null) return null;

        View findView = parentView.findViewById(id);
        if (findView != null) {
            return (T) findView;
        }

        return null;
    }

    private final class ComponentListener implements Player.EventListener, View.OnClickListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
//            updateRepeatModeButton();
//            updateNavigation();
        }

        @Override
        public void onPositionDiscontinuity() {
//            updateNavigation();
            updateProgress();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
//            updateNavigation();
//            updateTimeBarMode();
            updateProgress();
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            // Do nothing.
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }

        @Override
        public void onClick(View v) {
//            if (player != null) {
//                if (nextButton == view) {
//                    next();
//                } else if (previousButton == view) {
//                    previous();
//                } else if (fastForwardButton == view) {
//                    fastForward();
//                } else if (rewindButton == view) {
//                    rewind();
//                } else if (playButton == view) {
//                    controlDispatcher.dispatchSetPlayWhenReady(player, true);
//                } else if (pauseButton == view) {
//                    controlDispatcher.dispatchSetPlayWhenReady(player, false);
//                } else if (repeatToggleButton == view) {
//                    controlDispatcher.dispatchSetRepeatMode(player, RepeatModeUtil
// .getNextRepeatMode(
//                            player.getRepeatMode(), repeatToggleModes));
//                }
//            }
//            hideAfterTimeout();
            if (v.getId() == R.id.player_play_pause) {
                Log.e("TAG", "player_play_pause");
            } else if (v.getId() == R.id.player_full_screen) {
                Log.e("TAG", "player_full_screen");
            }
        }

    }


}
