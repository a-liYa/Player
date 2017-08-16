package com.aliya.player.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliya.player.PlayerManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout parent;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent = findViewById(R.id.player_parent);

        findViewById(R.id.btn_player).setOnClickListener(this);
        findViewById(R.id.btn_single).setOnClickListener(this);
        findViewById(R.id.btn_list).setOnClickListener(this);

        Log.e("TAG", "MainActivity " + getWindow());

    }

    @Override
    protected void onStop() {
        super.onStop();
//        mPlayerView.releasePlayer();
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
                    ((ViewGroup)tv.getParent()).removeView(tv);
                } else {
                    parent.addView(tv);
                }

                break;
            case R.id.btn_list:
                startActivity(new Intent(this, ListActivity.class));
                break;
            case R.id.btn_single:
                break;
        }
    }

}
