package com.aliya.player.ui;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.aliya.player.PlayerHelper;
import com.aliya.player.R;
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

import java.util.List;

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

    private SimpleExoPlayer player;
    private Controller controller;
    private PlayerHelper mHelper;
    private ComponentListener componentListener;

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
        setBackgroundResource(R.color.module_player_background);

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
        contentFrame.addView(surfaceView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        if (controller != null) {
            // 3、 add controller views
            inflate(context, controller.getLayoutRes(), this);

            controller.onViewCreate();
        }
    }

    public void setPlayerHelper(PlayerHelper helper) {
        mHelper = helper;
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

        MediaSource videoSource = mHelper.buildMediaSource(Uri.parse(url), null, bandwidthMeter);

        // 3. 准备播放.
        player.prepare(videoSource);

        // 4. 开始播放.
        player.setPlayWhenReady(true);

        int progress = ProgressCache.get().getCacheProgress(url);
        if (progress != ProgressCache.NO_VALUE && progress > 0) {
            player.seekTo(progress);
        }
    }

    public void setPlayer(SimpleExoPlayer player) {
        if (this.player == player) {
            return;
        }

        if (this.player != null) {
            this.player.clearTextOutput(componentListener);
            this.player.clearVideoListener(componentListener);

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

            player.setVideoListener(componentListener);
            player.setTextOutput(componentListener);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void releasePlayer() {
        if (player != null) {
            controller.cacheProgress();
            if (controller != null) {
                controller.setPlayer(null);
            }
            player.release();
            player.clearTextOutput(componentListener);
            player.clearVideoListener(componentListener);
            player = null;
        }
    }

    public boolean isRelease() {
        return player == null;
    }

    /**
     * 同步状态
     *
     * @param synced 被同步的对象
     */
    public void syncRegime(PlayerView synced) {
        if (controller != null && synced != null) {
            controller.syncRegime(synced.controller);
        }
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
