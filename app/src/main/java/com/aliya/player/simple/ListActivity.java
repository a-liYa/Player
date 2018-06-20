package com.aliya.player.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliya.player.Extra;
import com.aliya.player.PlayerCallback;
import com.aliya.player.PlayerManager;
import com.aliya.player.PlayerRequest;
import com.aliya.player.ui.PlayerView;

import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(null));
        Adapter adapter = new Adapter(Arrays.asList(VideoUrls.getUrls()));
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        List<String> mDatas;

        public Adapter(List<String> datas) {
            mDatas = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PlayerRequest {

        String mData;

        ImageView mIvBg;
        ImageView mIvPlayStart;
        FrameLayout mParent;
        TextView mTvTitle;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout_list, parent, false));
            mIvBg = (ImageView) itemView.findViewById(R.id.iv_bg);
            mIvPlayStart = (ImageView) itemView.findViewById(R.id.iv_play_start);
            mParent = (FrameLayout) itemView.findViewById(R.id.parent_player);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);

            mIvPlayStart.setOnClickListener(this);
            itemView.findViewById(R.id.ll_info).setOnClickListener(this);
        }

        public void setData(String data) {
            mData = data;
            mTvTitle.setText(mData);
            GlideApp.with(itemView.getContext()).load(mData).into(mIvBg);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_play_start:
                    PlayerManager.setPlayerRequest(mParent, this);
                    PlayerManager.get().play(mParent, mData, new String("position " + getAdapterPosition()));
                    PlayerManager.setPlayerCallback(mParent, new PlayerCallback() {
                        @Override
                        public void onPause(PlayerView view) {
                            Log.e("TAG", "onPause " + Extra.getExtraData(view));
                        }

                        @Override
                        public void onPlay(PlayerView view) {
                            Log.e("TAG", "onPlay " + Extra.getExtraData(view));
                        }

                        @Override
                        public void onFullscreenChange(boolean isFullscreen, PlayerView view) {
                            Log.e("TAG", "onFullscreenChange " + isFullscreen + " - " + Extra.getExtraData(view));
                        }

                        @Override
                        public void onMuteChange(boolean isMute, PlayerView view) {
                            Log.e("TAG", "onMuteChange " + isMute + " - " + Extra.getExtraData(view));
                        }
                    });
                    break;
                case R.id.ll_info:

                    break;
            }
        }

        @Override
        public boolean onRequest(final PlayerView playerView) {
            itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playerView.play(mData, true);
                }
            }, 1000);
            return true;
        }
    }
}
