package com.waterfairy.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by water_fairy on 2016/11/30.
 */

public class ToastUtils {
    private static Toast mToast;
    private static Context mContext;
//    private static TextView mTextView;

    public static void initToast(Context context) {
        mContext = context;
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
//        mTextView = new TextView(mContext);
//        mToast.setGravity(Gravity.CENTER, 0, 80);
//        mToast.setView(mTextView);
    }

    public static void show(int resId) {
        show(mContext.getString(resId));
    }

    public static void show(String content) {
//        mTextView.setText(content);
        mToast.setText(content);
        show();
    }

    private static void show() {
        mToast.show();
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    public static void showOnUiThread(Activity activity, final String content) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show(content);
            }
        });

    }
}
