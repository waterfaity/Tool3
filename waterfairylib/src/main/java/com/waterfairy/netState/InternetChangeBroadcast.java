package com.waterfairy.netState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by water_fairy on 2017/8/24.
 * 995637517@qq.com
 */

public class InternetChangeBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetUtils.getNetworkType(context);
    }
}
