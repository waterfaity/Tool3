package com.waterfairy.tool3;

import android.app.Application;

import com.waterfairy.utils.ToastUtils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/21
 * @Description:
 */

public class MyApp extends Application {
    private static Application MY_APP;

    @Override
    public void onCreate() {
        super.onCreate();
        MY_APP = this;
        ToastUtils.initToast(this);
    }

    public static Application getApp() {
        return MY_APP;
    }
}
