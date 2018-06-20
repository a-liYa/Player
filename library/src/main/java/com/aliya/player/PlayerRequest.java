package com.aliya.player;

import com.aliya.player.ui.PlayerView;

/**
 * 重新请求 MediaMeta 回调接口
 *
 * @author a_liYa
 * @date 2018/5/3 10:00.
 */
public interface PlayerRequest {

    boolean onRequest(PlayerView playerView);

}
