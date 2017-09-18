package com.aliya.player.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.aliya.player.FullscreenActivity;
import com.aliya.player.PlayerHelper;
import com.aliya.player.PlayerLifecycleImpl;
import com.aliya.player.PlayerListener;
import com.aliya.player.R;
import com.aliya.player.lifecycle.LifecycleUtils;
import com.aliya.player.ui.widget.AspectRatioFrameLayout;
import com.aliya.player.utils.ProgressCache;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 视频播放 View
 *
 * @author a_liYa
 * @date 2017/8/10 16:40.
 */
public class PlayerView extends FrameLayout {

    private View surfaceView;
    private AspectRatioFrameLayout contentFrame;

    private String mUrl;
    private boolean fullscreen;

    private SimpleExoPlayer player;
    private Controller controller;
    private PlayerHelper helper;
    private ComponentListener componentListener;
    private SoftReference<FrameLayout> backupParentSoft;
    private ExecutorService service;

    public PlayerLifecycleImpl playerLifecycle;

    public PlayerView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.BLACK);
        playerLifecycle = new PlayerLifecycleImpl(this);
        controller = new Controller(this);

        componentListener = new ComponentListener();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        // 1、add video view
        contentFrame = new AspectRatioFrameLayout(context);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(contentFrame, lp);

        // 2、add surfaceView to video view
        surfaceView = new SurfaceView(context);
        contentFrame.addView(surfaceView, MATCH_PARENT, MATCH_PARENT);

        if (controller != null) {
            // 3、 add controller views
            inflate(context, controller.getLayoutRes(), this);

            controller.onViewCreate();
        }
        service = PlayerHelper.getThreadExecutor();
    }

    public void setPlayerHelper(PlayerHelper helper) {
        this.helper = helper;
    }

    public void replay() {
        if (!TextUtils.isEmpty(mUrl)) play(mUrl);
    }

    public void play(String url) {
        mUrl = url;

        // 1. Create a default TrackSelector
        // 数据传输相关，传输速度、传输监听等
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the mPlayer
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

        setPlayer(player);

        MediaSource videoSource = helper.buildMediaSource(Uri.parse(url), null, bandwidthMeter);

        // 3. 准备播放.
        player.prepare(videoSource);

        // 4. 开始播放.
        player.setPlayWhenReady(true);

        int progress = ProgressCache.get().getCacheProgress(url);
        if (progress != ProgressCache.NO_VALUE && progress > 0) {
            player.seekTo(progress);
        }
    }

    public PlayerListener getPlayerListener() {
        if (getParent() instanceof View) {
            Object tag = ((View) getParent()).getTag(R.id.player_tag_listener);
            if (tag instanceof PlayerListener) {
                return (PlayerListener) tag;
            }
        }
        return null;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setPlayer(SimpleExoPlayer player) {
        if (this.player == player) {
            return;
        }

        if (this.player != null) {
            this.player.removeTextOutput(componentListener);
            this.player.removeVideoListener(componentListener);

            if (surfaceView instanceof SurfaceView) {
                this.player.clearVideoSurfaceView((SurfaceView) surfaceView);
            }
        }

        this.player = player;

        controller.setPlayer(player);

        if (player != null) {

            if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);
            }

            player.addVideoListener(componentListener);
            player.addTextOutput(componentListener);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /**
     * 停止播放，并释放player
     */
    public void stop() {
        if (player != null) {
            if (controller != null) {
                controller.cacheProgress();
                controller.setPlayer(null);
            }

            player.removeTextOutput(componentListener);
            player.removeVideoListener(componentListener);
            if (surfaceView instanceof SurfaceView) {
                player.clearVideoSurfaceView((SurfaceView) surfaceView);
            }

            // 必须在 #clearVideoSurfaceView 之后调用，解决异步带来的ANR
            service.execute(new ReleaseRunnable(player));

            player = null;
        }
    }

    public boolean isStop() {
        return player == null;
    }

    private class ReleaseRunnable implements Runnable {

        private SimpleExoPlayer player;

        public ReleaseRunnable(SimpleExoPlayer player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (player != null) {
                try {
                    player.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                player = null;
            }
        }
    }

    /**
     * 释放并从父布局删除
     */
    public void release() {
        stop();
        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        fullscreen = false;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * 同步状态(从另外一个对象中)
     *
     * @param synced 被同步的对象
     */
    public void syncRegime(PlayerView synced) {
        if (controller != null && synced != null) {
            fullscreen = synced.fullscreen;
            mUrl = synced.mUrl;
            backupParentSoft = synced.backupParentSoft;
            controller.syncRegime(synced.controller);
            playerLifecycle.setLifecycleFollowFlag(synced.playerLifecycle.isLifecycleFollowFlag());
            setKeepScreenOn(synced.getKeepScreenOn());
        }
    }

    public void switchFullScreen() {
        if (fullscreen) {
            exitFullscreen();
        } else {
            startFullScreen();
        }
    }

    public void startFullScreen() {
        fullscreen = true;
        LifecycleUtils.removeVideoLifecycle(this, playerLifecycle);
        ViewParent parent = getParent();
        if (parent instanceof FrameLayout) {
            backupParentSoft = new SoftReference<>((FrameLayout) parent);
        }
        FullscreenActivity.startActivity(helper.getContext(), mUrl);
        PlayerListener listener = getPlayerListener();
        if (listener != null) {
            listener.onChangeFullScreen(true);
        }
    }

    public void exitFullscreen() {
        fullscreen = false;
        LifecycleUtils.removeVideoLifecycle(this, playerLifecycle);
        PlayerListener listener = getPlayerListener();
        if (listener != null) {
            listener.onChangeFullScreen(false);
        }
        FrameLayout backup;
        if (backupParentSoft != null && (backup = backupParentSoft.get()) != null) {
            if (getParent() instanceof ViewGroup) {
                ((ViewGroup) getParent()).removeView(this);
                backup.addView(this, MATCH_PARENT, MATCH_PARENT);
            }
        }
        backupParentSoft = null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        controller.updateIcFullscreen();
        LifecycleUtils.addVideoLifecycle(this, playerLifecycle);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        fullscreen = false;
        LifecycleUtils.removeVideoLifecycle(this, playerLifecycle);
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener,
            TextRenderer.Output {

        // TextRenderer.Output implementation
        @Override
        public void onCues(List<Cue> cues) {
//            if (subtitleView != null) {
//                subtitleView.onCues(cues);
//            }
        }

        // SimpleExoPlayer.VideoListener implementation
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                       float pixelWidthHeightRatio) {
            if (contentFrame != null) {
                float aspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
                contentFrame.setAspectRatio(aspectRatio);
            }
        }

        @Override
        public void onRenderedFirstFrame() {
//            if (shutterView != null) {
//                shutterView.setVisibility(INVISIBLE);
//            }
        }

    }

}
