package com.waterfairy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by water_fairy on 2017/7/26.
 * 995637517@qq.com
 */

public class BaseDialog extends Dialog {
    protected View mRootView;

    protected BaseDialog(@NonNull Context context, @StyleRes int themeResId, int layoutResId) {
        super(context, themeResId);
        mRootView = LayoutInflater.from(context).inflate(layoutResId, null);
        addContentView(mRootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        initView();
        initData();
    }

    protected void initView() {

    }

    protected void initData() {

    }
}
