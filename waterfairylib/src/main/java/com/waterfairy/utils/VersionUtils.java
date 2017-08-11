package com.waterfairy.utils;

import android.content.Context;

/**
 * Created by water_fairy on 2017/5/4.
 * 995637517@qq.com
 */

public class VersionUtils {
    public static String getVersion(Context context){
        try {
            String pkName = context.getPackageName();
            return context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0";

    }
}
