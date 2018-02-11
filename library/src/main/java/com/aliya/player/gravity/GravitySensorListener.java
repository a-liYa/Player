package com.aliya.player.gravity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * 重力感应监听者
 *
 * @author a_liYa
 * @date 2018/2/9 10:58.
 */
public class GravitySensorListener implements SensorEventListener {

    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;

    public static final int ORIENTATION_UNKNOWN = -1;

    OrientationAngleChangeListener mChangeListener;

    public GravitySensorListener(OrientationAngleChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;

        int orientation = ORIENTATION_UNKNOWN;

        float X = -values[_DATA_X];
        float Y = -values[_DATA_Y];
        float Z = -values[_DATA_Z];
        float magnitude = X * X + Y * Y;
        // Don't trust the angle if the magnitude is small compared to the y value
        if (magnitude * 4 >= Z * Z) {
            float OneEightyOverPi = 57.29577957855f;
            float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
            orientation = 90 - Math.round(angle);
            // normalize to 0 - 359 range
            while (orientation >= 360) {
                orientation -= 360;
            }
            while (orientation < 0) {
                orientation += 360;
            }
        }

        if (mChangeListener != null) {
            mChangeListener.onOrientationAngleChange(orientation);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // no-op
    }


    interface OrientationAngleChangeListener {

        void onOrientationAngleChange(int angle);

    }

}