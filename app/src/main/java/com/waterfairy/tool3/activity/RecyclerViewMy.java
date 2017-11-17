package com.waterfairy.tool3.activity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/16
 * @Description:
 */

public class RecyclerViewMy extends RecyclerView {
    public RecyclerViewMy(Context context) {
        super(context);
    }

    public RecyclerViewMy(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
    }
}
