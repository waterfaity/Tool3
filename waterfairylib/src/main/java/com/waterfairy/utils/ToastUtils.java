package com.waterfairy.utils;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by water_fairy on 2016/11/30.
 * Update by water_fairy on 2017/10/12.
 */

public class ToastUtils {
    private static Toast mToast;
    private static Context mContext;
    private static TextView mTextView;
    public static final long SHORT_SHORT_TIME = 1000;
    public static final long SHORT_TIME = 2000;
    public static final long LONG_TIME = 3500;
    private static Timer timer;

    public static void initToast(Context context) {
        mContext = context;
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
//        mTextView = new TextView(mContext);
//        mToast.setGravity(Gravity.CENTER, 0, 80);
//        mToast.setView(mTextView);
    }


    /**
     * 默认show
     */
    public static void show(int resId) {
        show(mContext.getString(resId));
    }

    public static void show(String content) {
        //mTextView.setText(content);
        mToast.setText(content);
        show();
    }

    private static void show() {
        if (timer != null) {
            timer.cancel();
        }
        mToast.show();
    }


    /**
     * 有时间的show
     *
     * @param time
     */
    public static void show(int resId, long time) {
        show(mContext.getString(resId), time);
    }

    public static void show(String content, long time) {
        // mTextView.setText(content);
        mToast.setText(content);
        show(time);
    }

    private static void show(long time) {
        show();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cancelShow();
            }
        }, time);
    }

    /**
     * 取消显示
     */
    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    private static void cancelShow() {
        mToast.cancel();
    }
}
