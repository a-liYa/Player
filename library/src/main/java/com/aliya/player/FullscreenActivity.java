package com.aliya.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

    public static final String KEY_URL = "key_url";

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        frameContainer = (FrameLayout) findViewById(R.id.frame_container);
        PlayerManager.setPlayerListenerByView(frameContainer, listeners);

        initUrl(savedInstanceState);

        frameContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        playerManager = PlayerManager.get();
        playerManager.play(frameContainer, url);
        PlayerManager.setPlayerOnAttachStateChangeListener(frameContainer, listeners);

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
        super.onBackPressed();
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
        super.onBackPressed();
    }

    private void exitFullscreen() {
        if (playerManager != null && playerManager.getPlayerView() != null) {
            playerManager.getPlayerView().exitFullscreen();
        }
    }


}
