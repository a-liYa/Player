package com.aliya.player.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.aliya.player.FullscreenActivity;
import com.aliya.player.PlayerCallback;
import com.aliya.player.PlayerHelper;
import com.aliya.player.PlayerLifecycleImpl;
import com.aliya.player.PlayerListener;
import com.aliya.player.PlayerManager;
import com.aliya.player.PlayerRequest;
import com.aliya.player.R;
import com.aliya.player.lifecycle.LifecycleListener;
import com.aliya.player.lifecycle.LifecycleUtils;
import com.aliya.player.ui.widget.AspectRatioFrameLayout;
import com.aliya.player.utils.Recorder;
import com.aliya.player.utils.Utils;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 视频播放 View
 *
 * @author a_liYa
 * @date 2017/8/10 16:40.
 */
public class PlayerView extends FrameLayout implements ViewTreeObserver.OnPreDrawListener {

    private View surfaceView;
    private AspectRatioFrameLayout contentFrame;

    private String mUrl;

    private SimpleExoPlayer player;
    private Controller controller;
    private PlayerHelper helper;
    private ComponentListener componentListener;
    private SoftReference<FrameLayout> backupParentSoft;
    private ExecutorService service;

    public PlayerLifecycleImpl playerLifecycle;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private PlayerManager.OnUrlChangeListener mOnUrlChangeListener;

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
        // surfaceView = new SurfaceView(context);
        surfaceView = new TextureView(context);
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
        play(mUrl);
    }

    public void play(String url) {
        play(url, false);
    }

    public void play(String url, boolean ignoreRequest) {
        mUrl = url;

        if (mOnUrlChangeListener != null) mOnUrlChangeListener.onUrlChange(mUrl);

        // 拦截请求新地址
        ViewParent parent = getParent();
        if (!ignoreRequest && parent instanceof View) {
            Object tag = ((View) parent).getTag(R.id.player_tag_request);
            if (tag instanceof PlayerRequest) {
                if (((PlayerRequest) tag).onRequest(this)) {
                    controller.showBuffering();
                    return;
                }
            }
        }

        if (TextUtils.isEmpty(mUrl)) {
            controller.dispatchPlayerError();
            return;
        }

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

        MediaSource videoSource = helper.buildMediaSource(Uri.parse(url));

        // 3. 准备播放.
        player.prepare(videoSource);

        // 4. 开始播放.
        player.setPlayWhenReady(true);

        int progress = Recorder.get().getCacheProgress(url);
        if (progress != Recorder.NO_VALUE && progress > 0) {
            player.seekTo(progress);
        }

    }

    public Controller getController() {
        return controller;
    }

    /**
     * 请求音频焦点
     */
    public void requestAudioFocus() {
        AudioManager audioManager = (AudioManager)
                getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
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

    public void pause() {
        if (player != null) {
            player.setPlayWhenReady(false);
            PlayerCallback callback = PlayerManager.getPlayerCallback((View) getParent());
            if (callback != null)
                callback.onPause(this);
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public void setPlayer(SimpleExoPlayer player) {
        if (this.player == player) {
            return;
        }

        if (this.player != null) {
            this.player.removeVideoListener(componentListener);

            if (surfaceView instanceof SurfaceView) {
                this.player.clearVideoSurfaceView((SurfaceView) surfaceView);
            } else if (surfaceView instanceof TextureView) {
                this.player.clearVideoTextureView((TextureView) surfaceView);
            }
        }

        this.player = player;

        controller.setPlayer(player);

        if (player != null) {

            if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);
            } else if (surfaceView instanceof TextureView) {
                player.setVideoTextureView((TextureView) surfaceView);
            }

            player.addVideoListener(componentListener);
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

            player.removeVideoListener(componentListener);
            if (surfaceView instanceof SurfaceView) {
                player.clearVideoSurfaceView((SurfaceView) surfaceView);
            }
            if (surfaceView instanceof TextureView) {
                player.clearVideoTextureView((TextureView) surfaceView);
            }
            // 必须在 #clearVideoSurfaceView 之后调用，解决异步带来的ANR
            service.execute(new ReleaseRunnable(player));

            player = null;

            AudioManager audioManager = (AudioManager)
                    getContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    public boolean isStop() {
        return player == null;
    }

    public void setOnAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener listener) {
        mOnAudioFocusChangeListener = listener;
    }

    public void setOnUrlChangeListener(PlayerManager.OnUrlChangeListener listener) {
        mOnUrlChangeListener = listener;
    }

    private Rect mRect = new Rect();

    @Override
    public boolean onPreDraw() {
        if (playerLifecycle.isAtLeast(LifecycleListener.LifecycleState.RESUMED)) {
            if (!isShown() || !getGlobalVisibleRect(mRect)) {
                if (!PlayerManager.get().isFullScreenPost()) {
                    // post 为了防止SurfaceView#mScrollChangedListener内部Display.getDisplayId()空指针
                    post(new Runnable() {
                        @Override
                        public void run() {
                            release();
                        }
                    });
                }
            }
        }
        return true;
    }

    private class ReleaseRunnable implements Runnable {

        private SimpleExoPlayer mInnerPlayer;

        public ReleaseRunnable(SimpleExoPlayer player) {
            this.mInnerPlayer = player;
        }

        @Override
        public void run() {
            if (mInnerPlayer != null) {
                try {
                    mInnerPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mInnerPlayer = null;
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
        controller.reset();
    }

    public boolean isFullscreen() {
        ViewParent parent = getParent();
        if (parent instanceof View) {
            return Utils.getActivity(((View) parent).getContext()) instanceof FullscreenActivity;
        }
        return false;
    }

    /**
     * 同步状态(从另外一个对象中)
     *
     * @param synced 被同步的对象
     */
    public void syncRegime(PlayerView synced) {
        if (controller != null && synced != null) {
            mUrl = synced.mUrl;
            backupParentSoft = synced.backupParentSoft;
            controller.syncRegime(synced.controller);
            playerLifecycle.setLifecycleFollowFlag(synced.playerLifecycle.isLifecycleFollowFlag());
            setKeepScreenOn(synced.getKeepScreenOn());
        }
    }

    public void switchFullScreen() {
        if (isFullscreen()) {
            exitFullscreen();
        } else {
            startFullScreen();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (surfaceView instanceof SurfaceView) {
            // Work around https://github.com/google/ExoPlayer/issues/3160.
            surfaceView.setVisibility(visibility);
        }
    }

    public void startFullScreen() {
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
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LifecycleUtils.removeVideoLifecycle(this, playerLifecycle);
        getViewTreeObserver().removeOnPreDrawListener(this);
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener{

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
