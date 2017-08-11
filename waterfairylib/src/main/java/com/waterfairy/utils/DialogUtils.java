package com.waterfairy.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by water_fairy on 2017/5/5.
 * 995637517@qq.com
 */

public class DialogUtils {
    public AlertDialog show(Context context, String title, final OnDialogClickListener onDialogClickListener) {
        final AlertDialog alertDialog =
                (new AlertDialog.Builder(context))
                        .setTitle(title)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onDialogClickListener != null)
                                    onDialogClickListener.onDialogClick(false);
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onDialogClickListener != null)
                                    onDialogClickListener.onDialogClick(true);
                            }
                        }).create();
        alertDialog.show();

        return alertDialog;
    }

    public interface OnDialogClickListener {
        void onDialogClick(boolean tag);
    }
}
