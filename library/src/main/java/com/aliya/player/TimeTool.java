package com.aliya.player;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;

import java.util.Arrays;

/**
 * TimeTool : 计算时间
 * {@Seek com.google.android.exoplayer2.ui.PlaybackControlView#updateProgress()}
 *
 * @author a_liYa
 * @date 2017/8/11 15:32.
 */
final class TimeTool {

    long position = 0;
    long bufferedPosition = 0;
    long duration = 0;

    private boolean multiWindowTimeBar;
    private long[] adGroupTimesMs;
    private boolean[] playedAdGroups;

    private final Timeline.Period period;
    private final Timeline.Window window;

    public TimeTool() {

        period = new Timeline.Period();
        window = new Timeline.Window();

        adGroupTimesMs = new long[0];
        playedAdGroups = new boolean[0];
    }

    // 主要复杂在计算Google广告系统的时间问题
    public void calcTime(Player player) {
        position = 0;
        bufferedPosition = 0;
        duration = 0;

        if (player == null) return;

        long currentWindowTimeBarOffsetUs = 0;
        long durationUs = 0;
        int adGroupCount = 0;
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty()) {
            int currentWindowIndex = player.getCurrentWindowIndex();
            int firstWindowIndex = multiWindowTimeBar ? 0 : currentWindowIndex;
            int lastWindowIndex =
                    multiWindowTimeBar ? timeline.getWindowCount() - 1 : currentWindowIndex;
            for (int i = firstWindowIndex; i <= lastWindowIndex; i++) {
                if (i == currentWindowIndex) {
                    currentWindowTimeBarOffsetUs = durationUs;
                }
                timeline.getWindow(i, window);
                if (window.durationUs == C.TIME_UNSET) {
                    Assertions.checkState(!multiWindowTimeBar);
                    break;
                }
                for (int j = window.firstPeriodIndex; j <= window.lastPeriodIndex; j++) {
                    timeline.getPeriod(j, period);
                    int periodAdGroupCount = period.getAdGroupCount();
                    for (int adGroupIndex = 0; adGroupIndex < periodAdGroupCount;
                         adGroupIndex++) {
                        long adGroupTimeInPeriodUs = period.getAdGroupTimeUs(adGroupIndex);
                        if (adGroupTimeInPeriodUs == C.TIME_END_OF_SOURCE) {
                            if (period.durationUs == C.TIME_UNSET) {
                                // Don't show ad markers for postrolls in periods with
                                // unknown duration.
                                continue;
                            }
                            adGroupTimeInPeriodUs = period.durationUs;
                        }
                        long adGroupTimeInWindowUs = adGroupTimeInPeriodUs + period
                                .getPositionInWindowUs();
                        if (adGroupTimeInWindowUs >= 0 && adGroupTimeInWindowUs <= window
                                .durationUs) {
                            if (adGroupCount == adGroupTimesMs.length) {
                                int newLength = adGroupTimesMs.length == 0 ? 1 :
                                        adGroupTimesMs.length * 2;
                                adGroupTimesMs = Arrays.copyOf(adGroupTimesMs, newLength);
                                playedAdGroups = Arrays.copyOf(playedAdGroups, newLength);
                            }
                            adGroupTimesMs[adGroupCount] = C.usToMs(durationUs +
                                    adGroupTimeInWindowUs);
                            playedAdGroups[adGroupCount] = period.hasPlayedAdGroup
                                    (adGroupIndex);
                            adGroupCount++;
                        }
                    }
                }
                durationUs += window.durationUs;
            }
        }

        duration = C.usToMs(durationUs);
        position = C.usToMs(currentWindowTimeBarOffsetUs);
        bufferedPosition = position;
        if (player.isPlayingAd()) {
            position += player.getContentPosition();
            bufferedPosition = position;
        } else {
            position += player.getCurrentPosition();
            bufferedPosition += player.getBufferedPosition();
        }
    }

}
