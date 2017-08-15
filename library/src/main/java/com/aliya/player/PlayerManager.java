package com.aliya.player;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.aliya.player.ui.PlayerView;

import java.lang.ref.SoftReference;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Player manager.
 *
 * @author a_liYa
 * @date 2017/8/13 21:37.
 */
public class PlayerManager {

    private PlayerView mPlayerView;
    private PlayerView mSmoothPlayerView;
    private LayoutParams mPlayerLayoutParams;

    private FrameLayout mBackupParent;
    private String mBackupUrl;
    private PlayerHelper mHelper;

    private GroupListener mGroupListener;

    private volatile static SoftReference<PlayerManager> sSoftInstance;

    private PlayerManager() {
        mHelper = new PlayerHelper();
        mPlayerLayoutParams = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mGroupListener = new GroupListener();
    }

    public static PlayerManager get() {
        PlayerManager manager;
        if (sSoftInstance == null || (manager = sSoftInstance.get()) == null) {
            synchronized (PlayerManager.class) {
                if (sSoftInstance == null || (manager = sSoftInstance.get()) == null) {
                    sSoftInstance = new SoftReference<>(manager = new PlayerManager());
                }
            }
        }
        return manager;
    }

    public void play(FrameLayout parent, String url) {
        play(parent, url, -1);
    }

    public void play(FrameLayout parent, String url, int childIndex) {
        if (TextUtils.isEmpty(url) || parent == null) return;
        mBackupParent = parent;
        mHelper.setContext(parent.getContext());

        parent.removeOnAttachStateChangeListener(mGroupListener);
        parent.addOnAttachStateChangeListener(mGroupListener);

        if (TextUtils.equals(mBackupUrl, url) && mPlayerView != null && !mPlayerView.isRelease()) {
            // 同一个url, 且没释放; eg:全屏
            if (mSmoothPlayerView == null) {
                mSmoothPlayerView = new PlayerView(mHelper.getContext());
                mSmoothPlayerView.setPlayerHelper(mHelper);
                mSmoothPlayerView.setId(R.id.player_view);
            }

            mSmoothPlayerView.removeOnAttachStateChangeListener(mGroupListener);
            mSmoothPlayerView.addOnAttachStateChangeListener(mGroupListener);
            if (mPlayerView != null) {
                mPlayerView.removeOnAttachStateChangeListener(mGroupListener);
            }

            if (childIndex < 0) {
                parent.addView(mSmoothPlayerView, mPlayerLayoutParams);
            } else {
                if (childIndex > parent.getChildCount()) {
                    childIndex = parent.getChildCount();
                }
                parent.addView(mSmoothPlayerView, childIndex, mPlayerLayoutParams);
            }
            mSmoothPlayerView.post(smoothSwitchRunnable);
            Extra.setExtra(mSmoothPlayerView, url, null);

        } else { // 不同url

            if (mPlayerView == null) {
                mPlayerView = new PlayerView(mHelper.getContext());
                mPlayerView.setPlayerHelper(mHelper);
                mPlayerView.setId(R.id.player_view);
            }

            mPlayerView.releasePlayer();

            if (mPlayerView.getParent() != parent) {
                if (mPlayerView.getParent() instanceof ViewGroup) { // 从上一个依附控件中删除
                    ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
                }
                if (childIndex < 0) {
                    parent.addView(mPlayerView, mPlayerLayoutParams);
                } else {
                    if (childIndex > parent.getChildCount()) {
                        childIndex = parent.getChildCount();
                    }
                    parent.addView(mPlayerView, childIndex, mPlayerLayoutParams);
                }
            }

            mPlayerView.removeOnAttachStateChangeListener(mGroupListener);
            mPlayerView.addOnAttachStateChangeListener(mGroupListener);

            mBackupUrl = url;
            mPlayerView.play(url);

            Extra.setExtra(mPlayerView, url, null);
        }

    }

    private Runnable smoothSwitchRunnable = new Runnable() {
        @Override
        public void run() {
            smoothSwitchView();
        }
    };

    /**
     * 平滑的切换视频依赖的View
     */
    private void smoothSwitchView() {

        if (mPlayerView == mSmoothPlayerView || mPlayerView.getPlayer() == null) {
            return;
        }

        if (mSmoothPlayerView != null) {
            mSmoothPlayerView.setPlayer(mPlayerView.getPlayer());
            mSmoothPlayerView.syncRegime(mPlayerView);
        }

        if (mPlayerView != null) {
            mPlayerView.setPlayer(null);
        }

        // 交换两个PlayerView引用
        PlayerView temp = mSmoothPlayerView;
        mSmoothPlayerView = mPlayerView;
        mPlayerView = temp;

        // 从上一个依附控件中删除
        if (mSmoothPlayerView.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) mSmoothPlayerView.getParent();
            parent.removeOnAttachStateChangeListener(mGroupListener);
            parent.removeView(mSmoothPlayerView);
        }

    }

    public void exitFullscreen() {
        if (mPlayerView != null) {
            mPlayerView.exitFullscreen();
        }
    }

    class GroupListener implements View.OnAttachStateChangeListener{

        @Override
        public void onViewAttachedToWindow(View v) {
            if (v.getId() == R.id.player_view) {
                ViewGroup parent = (ViewGroup) v.getParent();
                // 监听视频控件父布局
                parent.addOnAttachStateChangeListener(this); // 持有VideoManager引用，防止软引用回收
//                Utils.addVideoLifecycle(v, mPlayerLifecycle);
//                Object tag = parent.getTag(TAG_KEY_ATTACH_LISTENER);
//                if (tag != null && tag instanceof View.OnAttachStateChangeListener) {
//                    ((View.OnAttachStateChangeListener) tag).onViewAttachedToWindow(v);
//                }
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (mPlayerView != null && mPlayerView.getParent() == v) {
                // 视频父容器被删除
                mPlayerView.releasePlayer();
                ((ViewGroup) v).removeView(mPlayerView);
            } else if (mSmoothPlayerView != null && mSmoothPlayerView.getParent() == v) {
                // 视频父容器被删除
                mSmoothPlayerView.releasePlayer();
                ((ViewGroup) v).removeView(mSmoothPlayerView);
            }  // else if (v.getId() == R.id.player_view) {
//                final ViewParent parent = v.getParent();
//                v.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 释放VideoManager的引用
//                        ((View) parent).removeOnAttachStateChangeListener(mGroupListener);
//                    }
//                });
//                Utils.removeVideoLifecycle(v);
//                Object tag = ((ViewGroup) v.getParent()).getTag(TAG_KEY_ATTACH_LISTENER);
//                if (tag != null && tag instanceof View.OnAttachStateChangeListener) {
//                    ((View.OnAttachStateChangeListener) tag).onViewDetachedFromWindow(v);
//                }
//            }
        }

    }

}
