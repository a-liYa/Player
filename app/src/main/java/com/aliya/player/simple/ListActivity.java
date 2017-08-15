package com.aliya.player.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliya.player.PlayerManager;

import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = findViewById(R.id.recycle);

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        String mData;

        ImageView mIvBg;
        ImageView mIvPlayStart;
        FrameLayout mParentPlayer;
        TextView mTvTitle;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout_list, parent, false));
            mIvBg = itemView.findViewById(R.id.iv_bg);
            mIvPlayStart = itemView.findViewById(R.id.iv_play_start);
            mParentPlayer = itemView.findViewById(R.id.parent_player);
            mTvTitle = itemView.findViewById(R.id.tv_title);

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
                    PlayerManager.get().play(mParentPlayer, mData);
                    break;
                case R.id.ll_info:

                    break;
            }
        }
    }
}
