package com.aliya.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.aliya.player.gravity.OrientationHelper;
import com.aliya.player.gravity.OrientationListener;
import com.aliya.player.ui.PlayerView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

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

    private String mBackupUrl;
    private PlayerHelper mHelper;
    private OrientationHelper mOrientationHelper;

    private GroupListener mGroupListener;

    private volatile static SoftReference<PlayerManager> sSoftInstance;

    private PlayerManager() {
        mHelper = new PlayerHelper();
        mOrientationHelper = new OrientationHelper();
        mPlayerLayoutParams = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mGroupListener = new GroupListener();

    }

    private static Map<Class<? extends Control>, Control.Factory> sFactoryMap = new HashMap();
    public static void registerControl(Class<? extends Control> key, Control.Factory factory) {
        sFactoryMap.put(key, factory);
    }

    public static Control.Factory getControlFactory(Class<? extends Control> key) {
        return sFactoryMap.get(key);
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
        play(parent, url, null);
    }

    public void play(FrameLayout parent, String url, Object extraData) {
        play(parent, url, -1, extraData);
    }

    public void play(FrameLayout parent, String url, int childIndex) {
        play(parent, url, childIndex, null);
    }

    public void play(FrameLayout parent, String url, int childIndex, Object extraData) {
        if (TextUtils.isEmpty(url) || parent == null) return;
        mHelper.setContext(parent.getContext());

        if (TextUtils.equals(mBackupUrl, url) && mPlayerView != null && !mPlayerView.isStop()) {
            // 同一个url, 且没释放; eg:全屏
            if (mSmoothPlayerView == null) {
                mSmoothPlayerView = new PlayerView(mHelper.getContext());
                mSmoothPlayerView.setPlayerHelper(mHelper);
                mSmoothPlayerView.setOnAudioFocusChangeListener(mGroupListener);
                mSmoothPlayerView.setId(R.id.player_view);
            }

            mSmoothPlayerView.removeOnAttachStateChangeListener(mGroupListener);
            mSmoothPlayerView.addOnAttachStateChangeListener(mGroupListener);

            if (childIndex < 0 || childIndex > parent.getChildCount()) { // 在最后一个位置插入
                parent.addView(mSmoothPlayerView, mPlayerLayoutParams);
            } else {
                parent.addView(mSmoothPlayerView, childIndex, mPlayerLayoutParams);
            }
            mSmoothPlayerView.post(mSmoothSwitchRunnable);
            if (extraData == null) { // 取复用View的数据
                extraData = Extra.getExtraData(mPlayerView);
            }
            Extra.setExtra(mSmoothPlayerView, url, extraData);

            setPlayerCallback(parent, getPlayerCallback((View) mPlayerView.getParent()));
        } else { // 不同url
            if (mPlayerView == null) {
                mPlayerView = new PlayerView(mHelper.getContext());
                mPlayerView.setPlayerHelper(mHelper);
                mPlayerView.setOnAudioFocusChangeListener(mGroupListener);
                mPlayerView.setId(R.id.player_view);
            }

            mPlayerView.stop();

            mPlayerView.removeOnAttachStateChangeListener(mGroupListener);
            mPlayerView.addOnAttachStateChangeListener(mGroupListener);

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

            mBackupUrl = url;
            mPlayerView.play(url);

            Extra.setExtra(mPlayerView, url, extraData);
        }

    }

    public PlayerView getPlayerView() {
        return mPlayerView;
    }

    public OrientationHelper getOrientationHelper() {
        return mOrientationHelper;
    }

    private Runnable mSmoothSwitchRunnable = new Runnable() {
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
            parent.removeView(mSmoothPlayerView);
        }

    }

    public void setLifecycleFollow(boolean lifecycleFollow) {
        if (mPlayerView != null && mPlayerView.playerLifecycle != null) {
            mPlayerView.playerLifecycle.setLifecycleFollowFlag(lifecycleFollow);
        }
    }

    public static void setPlayerCallback(View parent, PlayerCallback callback) {
        if (parent != null) {
            parent.setTag(R.id.player_tag_callback, callback);
        }
    }

    public static PlayerCallback getPlayerCallback(View parent) {
        if (parent != null) {
            return (PlayerCallback) parent.getTag(R.id.player_tag_callback);
        }
        return null;
    }

    static void setPlayerListenerByView(View parent, PlayerListener listener) {
        if (parent != null) {
            parent.setTag(R.id.player_tag_listener, listener);
        }
    }

    /**
     * 添加PlayerView的{@link View.OnAttachStateChangeListener}
     *
     * @param parent   父容器
     * @param listener .
     */
    public static void setPlayerOnAttachStateChangeListener(View parent, View
            .OnAttachStateChangeListener listener) {
        if (parent != null) {
            parent.setTag(R.id.player_tag_attach_listener, listener);
        }
    }

    private final class GroupListener implements View.OnAttachStateChangeListener,
            OrientationListener, AudioManager.OnAudioFocusChangeListener {

        private int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        private long timeMillis;

        @Override
        public void onViewAttachedToWindow(View v) {
            if (v.getId() == R.id.player_view) {
                ViewGroup parent = (ViewGroup) v.getParent();
                parent.removeOnAttachStateChangeListener(this);
                parent.addOnAttachStateChangeListener(this);

                // 持有PlayerManager引用，防止软引用回收
                parent.setTag(R.id.player_tag_reference, PlayerManager.this);

                Object tag = parent.getTag(R.id.player_tag_attach_listener);
                if (tag instanceof View.OnAttachStateChangeListener) {
                    ((View.OnAttachStateChangeListener) tag).onViewAttachedToWindow(v);
                }

                mOrientationHelper.registerListener(v.getContext(), this);
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (mPlayerView != null && mPlayerView.getParent() == v) {
                // 视频父容器被删除
                mPlayerView.release();
            } else if (mSmoothPlayerView != null && mSmoothPlayerView.getParent() == v) {
                // 视频父容器被删除
                mSmoothPlayerView.release();
            } else if (v.getId() == R.id.player_view) {
                final View parent = (View) v.getParent();
                parent.setTag(R.id.player_tag_reference, null); // 释放PlayerManager引用
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeOnAttachStateChangeListener(GroupListener.this);
                    }
                });
                Object tag = ((ViewGroup) v.getParent()).getTag(R.id.player_tag_attach_listener);
                if (tag instanceof View.OnAttachStateChangeListener) {
                    ((View.OnAttachStateChangeListener) tag).onViewDetachedFromWindow(v);
                }

                mOrientationHelper.unregisterListener(this);
            }
        }

        @Override
        public void onOrientation(int orientation) {
            if (screenOrientation != orientation) {
                if (mPlayerView == null || mPlayerView.isStop()) {
                    return;
                }
                try {
                    // 系统自动旋转关闭，屏幕不跟随重力感应
                    if (0 == Settings.System.getInt(
                            mHelper.getContext().getContentResolver(),
                            Settings.System.ACCELEROMETER_ROTATION)) {
                        return;
                    }
                } catch (Settings.SettingNotFoundException e) {
                    // no-op
                }

                if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    // 竖屏翻转 no-op
                } else {
                    if (SystemClock.uptimeMillis() - timeMillis < 1000) {
                        return; // 自动切换时间间隔太短
                    }
                    screenOrientation = orientation;
                    if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                        // 横屏 | 横屏翻转
                        if (mPlayerView.isFullscreen()) {
                            Intent intent = new Intent();
                            intent.setAction(FullscreenActivity.ACTION_ORIENTATION);
                            intent.putExtra(FullscreenActivity.KEY_ORIENTATION, orientation);
                            LocalBroadcastManager
                                    .getInstance(mHelper.getContext()).sendBroadcast(intent);
                        } else {
                            mPlayerView.startFullScreen();
                        }
                    } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        // 竖屏
                        if (mSmoothPlayerView != null) {
                            mSmoothPlayerView.removeCallbacks(mSmoothSwitchRunnable);
                        }
                        mPlayerView.exitFullscreen();
                    }
                    timeMillis = SystemClock.uptimeMillis();
                }
            }
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                // 音频焦点
                case AudioManager.AUDIOFOCUS_LOSS: // 其他App请求焦点，未知时长
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: // 其他App请求焦点，临时的
                    if (mPlayerView != null) {
                        mPlayerView.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: // 其他App请求焦点，临时的，可降低音量不用停止
                    break;
                case AudioManager.AUDIOFOCUS_GAIN: // 其他App放弃未知时长焦点
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT: // 其他App放弃临时焦点
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK: // 其他App放弃临时焦点
                    break;
            }
        }

        private String toText(int orientation) {
            switch (orientation) {
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    return "横屏翻转";
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    return "竖屏翻转";
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    return "横屏";
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    return "竖屏";

            }
            return "未知";
        }

    }

}
