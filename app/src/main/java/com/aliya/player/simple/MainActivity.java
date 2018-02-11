package com.aliya.player.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliya.player.gravity.OrientationHelper;
import com.aliya.player.gravity.OrientationListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OrientationListener {

    FrameLayout parent;

    TextView tv;
    private OrientationHelper mOrientationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent = (FrameLayout) findViewById(R.id.player_parent);

        findViewById(R.id.btn_player).setOnClickListener(this);
        findViewById(R.id.btn_single).setOnClickListener(this);
        findViewById(R.id.btn_list).setOnClickListener(this);

        mOrientationHelper = new OrientationHelper();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationHelper.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientationHelper.registerListener(this, this);
    }

    @Override
    protected void onStop() {
//        try {
//            // 系统自动旋转是否开启
//            int screenChange = Settings.System.getInt(getContentResolver(), Settings.System
//                    .ACCELEROMETER_ROTATION);
//            Log.e("TAG", "screenChange " + screenChange);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }


//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_player:
//                PlayerManager.get().play(parent, VideoUrls.getHttpsUrl());

                if (tv == null) {
                    tv = new TextView(this);
                    tv.setText("KeepScreenOn");
                    tv.setKeepScreenOn(true);
                }

                if (tv.getParent() != null) {
                    ((ViewGroup) tv.getParent()).removeView(tv);
                } else {
                    parent.addView(tv);
                }

                break;
            case R.id.btn_list:
                startActivity(new Intent(this, ListActivity.class));
                break;
            case R.id.btn_single:
//                mOrientationHelper.unregisterListener(this);
                break;
        }
    }

    private int screenOrientation;

    @Override
    public void onOrientation(int orientation) {
        if (screenOrientation != orientation) {
            screenOrientation = orientation;
            setRequestedOrientation(screenOrientation);
        }
    }

}
