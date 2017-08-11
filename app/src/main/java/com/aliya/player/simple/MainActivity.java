package com.aliya.player.simple;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.aliya.player.PlayerView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;

    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayerView = (PlayerView) findViewById(R.id.player_view);

        findViewById(R.id.btn_player).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (R.id.btn_player) {
            case R.id.btn_player:
                // 1. Create a default TrackSelector
                // 数据传输相关，传输速度、传输监听等
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                DefaultTrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                // 2. Create the mPlayer
                mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

                mPlayerView.setPlayer(mPlayer);

                MediaSource videoSource = buildMediaSource(Uri.parse(VideoUrls.getTestUrl()), null, bandwidthMeter);

                // 3. 准备播放.
                mPlayer.prepare(videoSource);

                // 4. 开始播放.
                mPlayer.setPlayWhenReady(true);
                break;
        }
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
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(getUserAgent(), bandwidthMeter);
    }

    protected String mUserAgent;

    private String getUserAgent() {
        if (mUserAgent == null) {
                return mUserAgent = Util.getUserAgent(this, "PlayerDemo");
        }
        return mUserAgent;
    }

}
