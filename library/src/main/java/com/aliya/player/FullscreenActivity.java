package com.aliya.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 全屏页面
 *
 * @author a_liYa
 * @date 2017/8/15 下午8:31.
 */
public class FullscreenActivity extends Activity {

    FrameLayout frameContainer;
    private String url;
    private PlayerManager playerManager;
    private Listeners listeners = new Listeners();
    private OrientationBroadcastReceiver mBroadcastReceiver;

    public static final String KEY_URL = "key_url";
    public static final String KEY_ORIENTATION = "key_orientation";
    public static final String ACTION_ORIENTATION = "com.aliya.action.ORIENTATION_CHANGE";

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, FullscreenActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(context, intent, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        playerManager = PlayerManager.get();
        // 设置屏幕取向
        if (playerManager.getOrientationHelper().isShouldReverseLandscape()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_palyer_activity_fullscreen);

        frameContainer = (FrameLayout) findViewById(R.id.frame_container);
        PlayerManager.setPlayerListenerByView(frameContainer, listeners);

        initUrl(savedInstanceState);

        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        frameContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        playerManager.play(frameContainer, url);
        PlayerManager.setPlayerOnAttachStateChangeListener(frameContainer, listeners);

        mBroadcastReceiver = new OrientationBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ORIENTATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void initUrl(Bundle intent) {
        if (intent != null) {
            url = intent.getString(KEY_URL);
        } else {
            url = getIntent().getStringExtra(KEY_URL);
        }
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        exitFullscreen();
    }

    private class Listeners implements PlayerListener, View.OnAttachStateChangeListener {

        @Override
        public void onChangeFullScreen(boolean isFullscreen) {
            if (!isFullscreen && !isFinishing()) {
                playerInnerExitFullscreen();
            }
        }

        @Override
        public void playEnded() {
            onBackPressed();
        }

        @Override
        public void onViewAttachedToWindow(View view) {

        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            if (!FullscreenActivity.this.isFinishing()) {
                FullscreenActivity.this.finish();
            }
        }

    }

    /**
     * 播放器内部退出全屏操作
     */
    private void playerInnerExitFullscreen() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onBackPressed();
    }

    private void exitFullscreen() {
        if (playerManager != null && playerManager.getPlayerView() != null) {
            playerManager.getPlayerView().exitFullscreen();
        }
    }

    class OrientationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int orientation = intent.getIntExtra(KEY_ORIENTATION, -1);
            if (orientation != -1) {
                FullscreenActivity.this.setRequestedOrientation(orientation);
            }
        }

    }

}
