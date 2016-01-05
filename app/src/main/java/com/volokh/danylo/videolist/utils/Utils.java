package com.volokh.danylo.videolist.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.utils.Logger;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    public static boolean isNetworkConnected(Context ctx) {
        boolean isNetworkConnected;
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        isNetworkConnected = ni != null;
        if(SHOW_LOGS) Logger.v(TAG, "isNetworkConnected, " + isNetworkConnected);
        return isNetworkConnected;
    }

}
