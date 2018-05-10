package com.aliya.player.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.aliya.player.Control;
import com.aliya.player.Extra;
import com.aliya.player.PlayerListener;
import com.aliya.player.R;
import com.aliya.player.ui.control.BottomProgressControl;
import com.aliya.player.ui.control.BufferControl;
import com.aliya.player.ui.control.CalcTime;
import com.aliya.player.ui.control.ErrorControl;
import com.aliya.player.ui.control.MobileNetControl;
import com.aliya.player.ui.control.MuteControl;
import com.aliya.player.ui.control.NavBarControl;
import com.aliya.player.utils.Recorder;
import com.aliya.player.utils.Utils;
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
    private BottomProgressControl bottomProgressControl;
    private MuteControl muteControl;
    private MobileNetControl mobileControl;

    private PlayerView playerView;

    private SimpleExoPlayer player;

    private ComponentListener componentListener;
    private CalcTime calcTime;
    private NetStateChangedReceiver netStateReceiver;

    private final Runnable updateProgressAction = new Runnable() {

        @Override
        public void run() {
            if (player == null) return;

            calcTime.calcTime(player);

            if (navBarControl != null) {
                navBarControl.updateProgress();
            }
            if (bottomProgressControl != null) {
                bottomProgressControl.updateProgress();
            }

            // Cancel any pending updates and schedule a new one if necessary.
            stopUpdateProgress();

            int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
            if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                long delayMs;
                if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                    delayMs = calcTime.calcSyncPeriod();
                } else {
                    delayMs = 1000;
                }
                if (playerView != null) {
                    playerView.postDelayed(updateProgressAction, delayMs);
                }
            }
        }
    };

    public Controller(PlayerView parentView) {
        this.playerView = parentView;
        componentListener = new ComponentListener();
        navBarControl = new NavBarControl(this);
        errorControl = new ErrorControl(this);
        bufferControl = new BufferControl(this);
        bottomProgressControl = new BottomProgressControl(this);
        muteControl = new MuteControl(this);
        mobileControl = new MobileNetControl(this);

        calcTime = new CalcTime();
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
        bottomProgressControl.onViewCreate(findViewById(playerView,
                R.id.player_bottom_progress_bar));
        muteControl.onViewCreate(findViewById(playerView, R.id.player_ic_volume));
        mobileControl.onViewCreate(findViewById(playerView, R.id.player_stub_mobile_network));

        bufferControl.setVisibilityListener(componentListener);
        navBarControl.setVisibilityListener(componentListener);
        errorControl.setVisibilityListener(componentListener);
        mobileControl.setVisibilityListener(componentListener);

        updateControlClickSwitch();

    }

    public void stopUpdateProgress() {
        if (playerView != null) {
            playerView.removeCallbacks(updateProgressAction);
        }
    }

    private void updateControlClickSwitch() {
        View.OnClickListener listener = componentListener;
        if (bufferControl != null && bufferControl.isVisible()) {
            listener = null;
        }
        if (errorControl != null && errorControl.isVisible()) {
            listener = null;
        }
        if (mobileControl != null && mobileControl.isVisible()) {
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
            unregisterNetStateChange();
            if (player != null) {
                setVisibilityControls(false, bufferControl, errorControl, mobileControl);
                player.addListener(componentListener);
                if (muteControl != null) {
                    muteControl.updateVolume();
                }
                registerNetStateChange();
            }
        }
    }

    public SimpleExoPlayer getPlayer() {
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
            Recorder.get().removeCacheProgress(Extra.getExtraUrl(playerView));
        } else {
            Recorder.get()
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
                navBarControl.updateIcPlayPause(player.getPlayWhenReady());
            }
        }

        if (errorControl != null && synced.errorControl != null) {
            errorControl.setVisibility(synced.errorControl.isVisible());
        }

        if (mobileControl != null) {
            mobileControl.syncRegime(synced.mobileControl);
        }

        updateProgressAction.run();

        updateIcFullscreen();
        updateControlClickSwitch();

        networkType = synced.networkType;

    }

    public void updateIcFullscreen() {
        if (navBarControl != null) {
            navBarControl.updateIcFullscreen();
        }
    }

    public CalcTime getCalcTime() {
        return calcTime;
    }

    private void registerNetStateChange() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (netStateReceiver == null) {
            netStateReceiver = new NetStateChangedReceiver();
        }
        if (getContext() != null) {
            getContext().registerReceiver(netStateReceiver, filter);
        }
    }

    private void unregisterNetStateChange() {
        if (netStateReceiver != null) {
            try {
                if (getContext() != null) {
                    getContext().unregisterReceiver(netStateReceiver);
                }
            } catch (Exception e) {
            }
            networkType = -1;
        }
    }

    public Context getContext() {
        if (playerView != null) {
            return playerView.getContext();
        }
        return null;
    }

    private void showMobileNetwork() {
        if (mobileControl != null) {
            if (!mobileControl.isVisible()) {
                mobileControl.setVisibility(true);
            }
            mobileControl.setTextHint(R.string.player_hint_mobile_network);
        }
    }

    private void showWiFiNetwork() {
        if (mobileControl != null) {
            if (mobileControl.isVisible()) {
                mobileControl.setTextHint(R.string.player_hint_wifi_network);
            }
        }
    }

    private final class ComponentListener implements Player.EventListener, View.OnClickListener,
            Control.VisibilityListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_BUFFERING) { // 缓冲
                stopUpdateProgress();
                if (!mobileControl.isVisible()) {
                    bufferControl.setVisibility(true);
                }
            } else if (playbackState == Player.STATE_READY) { // 播放
                updateProgressAction.run();
                bufferControl.setVisibility(false);
                if (playWhenReady) {
                    playerView.requestAudioFocus();
                }
            } else if (playbackState == Player.STATE_ENDED) { // 播完毕
                if (playerView != null) {
                    playerView.stop();
                    errorControl.showPlayEnded();
                    updateControlClickSwitch();
                    PlayerListener listener = playerView.getPlayerListener();
                    if (listener != null) {
                        listener.playEnded();
                    }
                }
            }

            if (!playWhenReady) { // 停止播放
                stopUpdateProgress();
            }

            if (playerView != null) {
                playerView.setKeepScreenOn(playWhenReady);
            }

            navBarControl.updateIcPlayPause(playWhenReady);
            updateControlClickSwitch();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
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
            // Do nothing. 切换视频源
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            cacheProgress();
            if (errorControl != null) {
                errorControl.setVisibility(true);
            }
            updateControlClickSwitch();

            if (playerView != null) {
                playerView.stop();
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
                    setVisibilityControls(false, navBarControl, bottomProgressControl);
                } else if (control == errorControl) {
                    setVisibilityControls(false, navBarControl, bufferControl,
                            bottomProgressControl);
                } else if (control == navBarControl) {
                    setVisibilityControls(false, bottomProgressControl);
                    setVisibilityControls(true, muteControl);
                } else if (control == mobileControl) {
                    setVisibilityControls(false, navBarControl, bufferControl,
                            bottomProgressControl);
                }
            } else {
                if (control == errorControl || control == mobileControl) {
                    updateControlClickSwitch();
                } else if (control == navBarControl) {
                    if (!errorControl.isVisible() && !bufferControl.isVisible()) {
                        setVisibilityControls(true, bottomProgressControl);
                    }
                    setVisibilityControls(false, muteControl);
                } else if (control == bufferControl) {
                    if (!navBarControl.isVisible()) {
                        setVisibilityControls(true, bottomProgressControl);
                    }
                } else if (control == errorControl) {
                    if (!navBarControl.isVisible()) {
                        setVisibilityControls(true, bottomProgressControl);
                    }
                }
            }
        }
    }

    private int networkType = -1;

    /**
     * 网络状态的变化监听
     */
    class NetStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    int type = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                    if (type != networkType) {
                        if (type == ConnectivityManager.TYPE_MOBILE) {
                            // 移动网络有变化，切走或切回
                            if (Utils.isMobile(context)) { // 切换到移动网络
                                showMobileNetwork();
                            }
                        } else if (type == ConnectivityManager.TYPE_WIFI) {
                            if (Utils.isWifi(context)) {
                                showWiFiNetwork();
                            }
                        }
                    }
                    networkType = type;
                    break;
            }
        }
    }

}
