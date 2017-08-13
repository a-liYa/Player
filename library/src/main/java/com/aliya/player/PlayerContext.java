package com.aliya.player;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.exoplayer2.util.Util;

/**
 * Player context
 *
 * @author a_liYa
 * @date 2017/8/13 21:51.
 */
class PlayerContext {

    private Context context;

    private String userAgent;

    public PlayerContext() {
    }

    public void setContext(Context context) {
        if (context == null) return;

        this.context = context.getApplicationContext();

        if (userAgent == null) {
            userAgent = Util.getUserAgent(context, getAppName());
        }
    }

    public Context getContext() {
        return context;
    }

    public String getUserAgent() {
        return userAgent;
    }

    private String getAppName() {
        if (context != null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo pkg = packageManager.getPackageInfo(context.getPackageName(), 0);

                return pkg.applicationInfo.loadLabel(packageManager).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "Player";
    }
}
