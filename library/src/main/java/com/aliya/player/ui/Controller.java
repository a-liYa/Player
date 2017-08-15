package com.aliya.player.ui;

import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;

import com.aliya.player.Control;
import com.aliya.player.Extra;
import com.aliya.player.R;
import com.aliya.player.utils.ProgressCache;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import static com.aliya.player.utils.Utils.findViewById;
import static com.aliya.player.utils.Utils.setVisibilityControls;

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

    private PlayerView playerView;

    private Player player;

    private final ComponentListener componentListener;

    public Controller(PlayerView parentView) {
        this.playerView = parentView;
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
        bufferControl.onViewCreate(findViewById(playerView, R.id.player_buffer_progress));
        navBarControl.onViewCreate(findViewById(playerView, R.id.player_control_bar));
        errorControl.onViewCreate(findViewById(playerView, R.id.player_stub_play_error));

        bufferControl.setVisibilityListener(componentListener);
        navBarControl.setVisibilityListener(componentListener);
        errorControl.setVisibilityListener(componentListener);

        updateControlVisibilityCanSwitch();
    }

    private void updateControlVisibilityCanSwitch() {
        View.OnClickListener listener = componentListener;
        if (bufferControl != null && bufferControl.isVisible()) {
            listener = null;
        }
        if (errorControl != null && errorControl.isVisible()) {
            listener = null;
        }

        if (playerView != null) {
            playerView.setOnClickListener(listener);
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

    public PlayerView getPlayerView() {
        return playerView;
    }

    public void seekTo(long positionMs) {
        if (player != null) {
            player.seekTo(positionMs);
        }
    }

    public void cacheProgress() {
        if (player == null
                || player.getCurrentPosition() == C.TIME_UNSET
                || player.getDuration() == C.TIME_UNSET) return;

        if (Math.abs(player.getDuration() - player.getCurrentPosition()) < 1000) {
            ProgressCache.get().removeCacheProgress(Extra.getExtraUrl(playerView));
        } else {
            ProgressCache.get()
                    .putCacheProgress(Extra.getExtraUrl(playerView), player.getCurrentPosition());
        }

    }

    /**
     * 同步状态
     *
     * @param synced 被同步的对象
     */
    public void syncRegime(Controller synced) {
        if (synced == null || this == synced) return;

        if (bufferControl != null && synced.bufferControl != null) {
            bufferControl.setVisibility(synced.bufferControl.isVisible());
        }

        if (navBarControl != null && synced.navBarControl != null) {
            navBarControl.setVisibility(synced.navBarControl.isVisible());
            if (player != null) {
                navBarControl.updatePlayPause(player.getPlayWhenReady());
            }
        }

        if (errorControl != null && synced.errorControl != null) {
            errorControl.setVisibility(synced.errorControl.isVisible());
        }

        updateIcFullscreen(playerView.isFullscreen());

    }

    public void updateIcFullscreen(boolean fullscreen) {
        if (navBarControl != null) {
            navBarControl.updateIcFullscreen(fullscreen);
        }
    }

    private final class ComponentListener implements Player.EventListener, View.OnClickListener,
            Control.VisibilityListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
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
                if (playerView != null) {
                    playerView.releasePlayer();
                }
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
            cacheProgress();
            if (errorControl != null) {
                errorControl.setVisibility(true);
            }
            updateControlVisibilityCanSwitch();

            if (playerView != null) {
                playerView.releasePlayer();
            }
        }

        @Override
        public void onClick(View v) {
            if (player != null) {
                if (v == playerView) {
                    if (navBarControl != null) navBarControl.switchVisibility();
                }
            }
        }

        @Override
        public void onVisibilityChange(Control control, boolean isVisible) {
            if (isVisible) {
                if (control == bufferControl) {
                    setVisibilityControls(false, navBarControl);
                } else if (control == errorControl) {
                    setVisibilityControls(false, navBarControl, bufferControl);
                }
            } else {
                if (control == errorControl) {
                    updateControlVisibilityCanSwitch();
                }
            }
        }
    }

}
