package com.waterfairy.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/13
 * @Description:
 */

public class DisplayMetricsUtils {
    private static final String TAG = "DisplayMetrics";

    public static void logMetricsInfo(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        String content = "";
        float density = displayMetrics.density;
        content += (("widthPx:\t" + displayMetrics.widthPixels) + "\n");
        content += (("heightPx:\t" + displayMetrics.heightPixels) + "\n");

        content += (("density:\t" + displayMetrics.density) + "\n");

        content += (("widthDp:\t" + (int) (displayMetrics.widthPixels / density)) + "\n");
        content += (("heightDp:\t" + (int) (displayMetrics.heightPixels / density)) + "\n");

        Log.i(TAG, "logMetricsInfo: \n" + content);
    }
}
