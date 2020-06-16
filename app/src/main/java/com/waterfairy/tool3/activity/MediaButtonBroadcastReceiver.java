package com.waterfairy.tool3.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/6/5 18:04
 * @info:
 */
class MediaButtonBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaButton";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: "+intent.getAction());
    }
}
