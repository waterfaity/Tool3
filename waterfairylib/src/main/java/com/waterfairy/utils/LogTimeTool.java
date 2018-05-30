package com.waterfairy.utils;

import android.util.Log;

import java.util.Date;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/5/3
 * @Description:
 */
public class LogTimeTool {
    private static final String TAG = "LogTimeTool";
    private static LogTimeTool logTimeTool;
    private static long time;

    private LogTimeTool() {
    }

    public static LogTimeTool getInstance() {
        if (logTimeTool == null) logTimeTool = new LogTimeTool();
        return logTimeTool;
    }

    public void logTime() {
        long timeTemp = new Date().getTime();
        Log.i(TAG, "logTime: " + (timeTemp - time));
        time = timeTemp;
    }
}
