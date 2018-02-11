package com.aliya.player.gravity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏幕感应方向 助手类
 *
 * @author a_liYa
 * @date 2018/2/11 10:28.
 */
public class OrientationHelper {

    private SensorManager sm;
    private GravitySensorListener mSensorListener;

    private int mScreenOrientation;
    private List<OrientationListener> mListeners;

    public OrientationHelper() {
    }

    public void registerListener(Context context, OrientationListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        boolean empty = mListeners.isEmpty();
        mListeners.add(listener);
        if (empty) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mSensorListener = new GravitySensorListener(new OrientationAngleChangeListener());
            sm.registerListener(mSensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregisterListener(OrientationListener listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
        boolean empty = mListeners != null ? mListeners.isEmpty() : true;
        if (empty) {
            if (sm != null) {
                sm.unregisterListener(mSensorListener);
            }
        }
    }

    private void onOrientation(int screenOrientation) {
        mScreenOrientation = screenOrientation;
        if (mListeners != null) {
            for (OrientationListener listener : mListeners) {
                listener.onOrientation(screenOrientation);
            }
        }
    }

    /**
     * 获取屏幕应该的取向
     *
     * @return {@link com.aliya.player.gravity.OrientationListener.ScreenOrientation}
     */
    public int getShouldScreenOrientation() {
        return mScreenOrientation;
    }

    class OrientationAngleChangeListener implements GravitySensorListener
            .OrientationAngleChangeListener {

        @Override
        public void onOrientationAngleChange(int angle) {
            if (angle > 45 && angle < 135) { // 横屏翻转
                onOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            } else if (angle > 135 && angle < 225) { // 竖屏翻转
                onOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            } else if (angle > 225 && angle < 315) { // 横屏
                onOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if ((angle > 315 && angle < 360) ||
                    (angle > 0 && angle < 45)) { // 竖屏
                onOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

    }

}
