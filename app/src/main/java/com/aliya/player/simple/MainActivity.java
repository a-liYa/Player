package com.aliya.player.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliya.player.PlayerManager;
import com.aliya.player.gravity.OrientationListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OrientationListener {

    FrameLayout parent;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent = (FrameLayout) findViewById(R.id.player_parent);

        findViewById(R.id.btn_player).setOnClickListener(this);
        findViewById(R.id.btn_single).setOnClickListener(this);
        findViewById(R.id.btn_list).setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
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
                PlayerManager.get().play(parent, "http://139.215.192.12/6975C1F05653282A8948EA5A78/03000A01005B1F882C392A856210078343B256-C2E6-4A6E-B82E-A28C734ED57B.mp4?ccode=0502&duration=84&expire=18000&psid=83ce5b2ddd399b1011913a5a331cc37b&sp=&ups_client_netip=77fe5c33&ups_ts=1528852009&ups_userid=462796718&utid=w0iSE01E0icCAXf%2BXDOlFv5B&vid=XMzY2MDMzMzU1Ng%3D%3D&vkey=Bddf6124d2128f50651729970ff0ea68c");
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
