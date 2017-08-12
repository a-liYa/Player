package com.aliya.player;

import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import static com.aliya.player.utils.VideoUtils.findViewById;

/**
 * Controller
 *
 * @author a_liYa
 * @date 2017/8/11 13:05.
 */
public class Controller {

    private ProgressBar bufferProgress;

    private ControlBar controlBar;

    private ViewGroup parentView;

    private Player player;


    private final ComponentListener componentListener;

    public Controller(ViewGroup parentView) {
        this.parentView = parentView;
        componentListener = new ComponentListener();
        controlBar = new ControlBar(this);
        if (parentView != null) {
            parentView.setOnClickListener(componentListener);
        }
    }

    public
    @LayoutRes
    int getLayoutRes() {
        return R.layout.module_player_layout_controller;
    }

    public void onViewCreate() {
        bufferProgress = findViewById(parentView, R.id.player_buffer_progress);
        controlBar.bindView(findViewById(parentView, R.id.player_control_bar));

    }

    private void setBufferProgressVisibility(boolean isVisible) {
        if (bufferProgress != null) {
            if (isVisible && bufferProgress.getVisibility() != View.VISIBLE) {
                bufferProgress.setVisibility(View.VISIBLE);
            } else if (!isVisible && bufferProgress.getVisibility() != View.INVISIBLE) {
                bufferProgress.setVisibility(View.INVISIBLE);
            }
            if (isVisible) { // 缓冲加载时，隐藏 control bar
                if (controlBar != null) controlBar.hide();
            }
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

    private final class ComponentListener implements Player.EventListener, View.OnClickListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            updatePlayPauseButton();

            if (playbackState == Player.STATE_BUFFERING) { // 缓冲
                if (controlBar != null) {
                    controlBar.stopUpdateProgress();
                }
                setBufferProgressVisibility(true);
            } else if (playbackState == Player.STATE_READY) { // 播放
                if (controlBar != null) {
                    controlBar.updateProgress();
                }
                setBufferProgressVisibility(false);
            } else if (playbackState == Player.STATE_ENDED) { // 播完毕

            }

            if (controlBar != null) {
                controlBar.updatePlayPause(playWhenReady);
            }

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.e("TAG", "onRepeatModeChanged " + repeatMode);
        }

        @Override
        public void onPositionDiscontinuity() { // position 不连续
            if (controlBar != null) {
                controlBar.updateProgress();
            }
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
            Log.e("TAG", "onPlaybackParametersChanged ");
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            if (controlBar != null) {
                controlBar.updateProgress();
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
            // Do nothing.
            Log.e("TAG", "onPlayerError " + error);
        }

        @Override
        public void onClick(View v) {

            if (player != null) {
                if (v == parentView) {
                    if (bufferProgress == null || bufferProgress.getVisibility() != View.VISIBLE) {
                        if (controlBar != null) controlBar.switchVisibility();
                    }
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

    }

}
