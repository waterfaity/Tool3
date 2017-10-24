package com.waterfairy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by water_fairy on 2017/5/12.
 * 995637517@qq.com
 */

public class NetUtils {
    public static boolean checkNet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    public static boolean showNet(Context context) {
        if (!checkNet(context)) {
            ToastUtils.show("没有网络了,检查下你的网络吧");
            return false;
        }
        return true;
    }
}
