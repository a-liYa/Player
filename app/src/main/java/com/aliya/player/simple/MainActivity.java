package com.aliya.player.simple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliya.player.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    protected void onStop() {
        super.onStop();
//        mPlayerView.stop();
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
                IntentFilter filter = new IntentFilter();
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                NetStateChangedReceiver netStateReceiver = new NetStateChangedReceiver();

                registerReceiver(netStateReceiver, filter);
                break;
        }
    }

    /**
     * 网络状态的变化监听
     */
    class NetStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    Log.e("TAG",
                            "network_type : " + intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1) +
                            "\nis_failover : " + intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false) +
                            "\nno_connectivity : " + intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) +
                            "\nextra_info : " + intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO) +
                            "\nnetwork_info : " + intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO)
                    );

//                    if (intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1)
//                            == ConnectivityManager.TYPE_MOBILE) {
//                        // 移动网络有变化，切走或切回
//                        if (Utils.isMobile(context)) { // 切换到移动网络
//                            Log.e("TAG", "移动网 哈哈");
//                        }
//                    }
                    break;
            }
        }

    }

}
