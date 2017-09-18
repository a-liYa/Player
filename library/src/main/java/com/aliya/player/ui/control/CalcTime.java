package com.aliya.player.ui.control;

import com.aliya.player.utils.Utils;
import com.google.android.exoplayer2.Player;

/**
 * CalcTime : 计算时间工具类
 *
 * @author a_liYa
 * @date 2017/8/11 15:32.
 */
public final class CalcTime {

    long position = 0;
    long bufferedPosition = 0;
    long duration = 0;

    public CalcTime() {
    }

    public void calcTime(Player player) {
        position = 0;
        bufferedPosition = 0;
        duration = 0;

        if (player == null) return;

        position = player.getCurrentPosition();
        bufferedPosition = player.getBufferedPosition();
        duration = player.getDuration();

    }

    public int calcProgress(int max) {
        if (duration == 0 ) {
            return 0;
        }
        return (int) (max * position / duration + 0.5f);
    }

    public int calcSecondaryProgress(int max) {
        if (duration == 0) {
            return 0;
        }
        return (int) (max * bufferedPosition / duration + 0.5f);
    }

    public String formatPosition() {
        return Utils.formatTime(position);
    }

    public String formatDuration() {
        return Utils.formatTime(duration);
    }

    /**
     * 计算同步周期时长 预防进度1s更新延迟问题
     *
     * @return delayMs
     */
    public long calcSyncPeriod() {
        long delayMs = 1000 - (position % 1000);
        return delayMs < 200 ? delayMs + 200 : delayMs;
    }

}
