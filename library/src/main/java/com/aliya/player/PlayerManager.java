package com.aliya.player;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.aliya.player.ui.PlayerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.lang.ref.SoftReference;

/**
 * Player manager.
 *
 * @author a_liYa
 * @date 2017/8/13 21:37.
 */
public class PlayerManager {

    private SimpleExoPlayer mPlayer;
    private Handler mainHandler;
    private PlayerContext mPlayerContext;

    private volatile static SoftReference<PlayerManager> sSoftInstance;

    private PlayerManager() {
        mPlayerContext = new PlayerContext();
    }

    public static PlayerManager get() {
        PlayerManager manager = null;
        if (sSoftInstance == null || (manager = sSoftInstance.get()) == null) {
            synchronized (PlayerManager.class) {
                if (sSoftInstance == null || (manager = sSoftInstance.get()) == null) {
                    sSoftInstance = new SoftReference<>(manager = new PlayerManager());
                }
            }
        }
        return manager;
    }

    public void play(String url, PlayerView playerView) {
        if (TextUtils.isEmpty(url) || playerView == null) return;

        mPlayerContext.setContext(playerView.getContext());

        // 1. Create a default TrackSelector
        // 数据传输相关，传输速度、传输监听等
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the mPlayer
        mPlayer = ExoPlayerFactory.newSimpleInstance(mPlayerContext.getContext(), trackSelector);

        playerView.setPlayer(mPlayer);

        MediaSource videoSource = buildMediaSource(Uri.parse(url), null, bandwidthMeter);

        // 3. 准备播放.
        mPlayer.prepare(videoSource);

        // 4. 开始播放.
        mPlayer.setPlayWhenReady(true);

    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension, DefaultBandwidthMeter
            bandwidthMeter) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(bandwidthMeter),
                        new DefaultSsChunkSource.Factory(buildDataSourceFactory(bandwidthMeter)),
                        mainHandler, null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(bandwidthMeter),
                        new DefaultDashChunkSource.Factory(buildDataSourceFactory(bandwidthMeter)),
                        mainHandler,
                        null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, buildDataSourceFactory(bandwidthMeter), mainHandler,
                        null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, buildDataSourceFactory(bandwidthMeter), new
                        DefaultExtractorsFactory(),
                        mainHandler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(mPlayerContext.getContext(), bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(mPlayerContext.getUserAgent(), bandwidthMeter);
    }

}
