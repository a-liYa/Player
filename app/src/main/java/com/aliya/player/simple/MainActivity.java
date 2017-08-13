package com.aliya.player.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aliya.player.PlayerManager;
import com.aliya.player.ui.PlayerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PlayerView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayerView = (PlayerView) findViewById(R.id.player_view);

        findViewById(R.id.btn_player).setOnClickListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayerView.releasePlayer();
    }

    @Override
    public void onClick(View view) {
        switch (R.id.btn_player) {
            case R.id.btn_player:
                PlayerManager.get().play(VideoUrls.getHttpsUrl(), mPlayerView);
                break;
        }
    }

}
