package com.waterfairy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/10/10
 * @Description:
 */

public class MyGridViewForScrollview extends GridView {
    public MyGridViewForScrollview(Context context) {
        super(context);
    }

    public MyGridViewForScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
