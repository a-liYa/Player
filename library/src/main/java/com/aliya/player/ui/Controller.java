package com.aliya.player.ui;

import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.aliya.player.Control;
import com.aliya.player.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import static com.aliya.player.utils.VideoUtils.findViewById;
import static com.aliya.player.utils.VideoUtils.setVisibilityControls;

/**
 * Controller
 *
 * @author a_liYa
 * @date 2017/8/11 13:05.
 */
public class Controller {

    private BufferControl bufferControl;
    private NavBarControl navBarControl;
    private ErrorControl errorControl;

    private ViewGroup parentView;

    private Player player;

    private final ComponentListener componentListener;

    public Controller(ViewGroup parentView) {
        this.parentView = parentView;
        componentListener = new ComponentListener();
        navBarControl = new NavBarControl(this);
        errorControl = new ErrorControl(this);
        bufferControl = new BufferControl(this);
    }

    public
    @LayoutRes
    int getLayoutRes() {
        return R.layout.module_player_layout_controller;
    }

    public void onViewCreate() {
        bufferControl.onViewCreate(findViewById(parentView, R.id.player_buffer_progress));
        navBarControl.onViewCreate(findViewById(parentView, R.id.player_control_bar));
        errorControl.onViewCreate(findViewById(parentView, R.id.player_stub_play_error));

        bufferControl.setVisibilityListener(componentListener);
        navBarControl.setVisibilityListener(componentListener);
        errorControl.setVisibilityListener(componentListener);

        updateControlVisibilityCanSwitch();
    }

//    private void setBufferProgressVisibility(boolean isVisible) {
//        if (bufferControl != null) {
//            bufferControl.setVisibility(isVisible);
//        }
//        if (isVisible) { // 缓冲加载时，隐藏 nav bar
//            if (navBarControl != null) navBarControl.setVisibility(false);
//        }
//    }

    private void updateControlVisibilityCanSwitch() {
        View.OnClickListener listener = componentListener;
        if (bufferControl != null && bufferControl.isVisible()) {
            listener = null;
        }
        if (errorControl != null && errorControl.isVisible()) {
            listener = null;
        }

        if (parentView != null) {
            parentView.setOnClickListener(listener);
        }
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

    public Player getPlayer() {
        return player;
    }

    public void seekTo(long positionMs) {
        if (player != null) {
            player.seekTo(positionMs);
        }
    }

    private final class ComponentListener implements Player.EventListener, View.OnClickListener,
            Control.VisibilityListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            updatePlayPauseButton();
            if (playbackState == Player.STATE_BUFFERING) { // 缓冲
                if (navBarControl != null) {
                    navBarControl.stopUpdateProgress();
                }
                bufferControl.setVisibility(true);
            } else if (playbackState == Player.STATE_READY) { // 播放
                if (navBarControl != null) {
                    navBarControl.updateProgress();
                }
                bufferControl.setVisibility(false);
            } else if (playbackState == Player.STATE_ENDED) { // 播完毕

            }

            navBarControl.updatePlayPause(playWhenReady);

            updateControlVisibilityCanSwitch();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.e("TAG", "onRepeatModeChanged " + repeatMode);
        }

        @Override
        public void onPositionDiscontinuity() { // position 不连续
            if (navBarControl != null) {
                navBarControl.updateProgress();
            }
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
            Log.e("TAG", "onPlaybackParametersChanged ");
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            if (navBarControl != null) {
                navBarControl.updateProgress();
            }
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            // Do nothing.
            Log.e("TAG", "onTracksChanged ");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (errorControl != null) {
                errorControl.setVisibility(true);
            }
            updateControlVisibilityCanSwitch();
        }

        @Override
        public void onClick(View v) {
            if (player != null) {
                if (v == parentView) {
                    if (navBarControl != null) navBarControl.switchVisibility();
                }
            }
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
//            hideAfterTimeout();
        }

        @Override
        public void onVisibilityChange(Control control, boolean isVisible) {
            if (isVisible) {
                if (control == bufferControl) {
                    setVisibilityControls(false, navBarControl);
                } else if (control == errorControl) {
                    setVisibilityControls(false, navBarControl, bufferControl);
                }
            }
        }
    }

}
