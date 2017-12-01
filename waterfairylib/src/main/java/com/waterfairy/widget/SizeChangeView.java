package com.waterfairy.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by water_fairy on 2017/5/5.
 * 995637517@qq.com
 * 尺寸变化时的监听view
 */

public class SizeChangeView extends View {
    private OnScreenSizeChangListener onScreenSizeChangListener;
    private OnLayoutChangListener onLayoutChangListener;
    private int width, height;

    private static final String TAG = "sizeChangeView";

    public SizeChangeView(Context context) {
        super(context);
    }

    public SizeChangeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        Log.i(TAG, "onLayout: " + changed + "--" + left + "--" + top + "--" + right + "--" + bottom);
        if (onLayoutChangListener != null)
            onLayoutChangListener.onChange(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!(width == w && height == h)) {
            width = w;
            height = h;
            Log.i(TAG, "onSizeChanged: " + width + "--" + height);
            if (onScreenSizeChangListener != null) {
                onScreenSizeChangListener.onChange(width, height);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface OnScreenSizeChangListener {
        void onChange(int width, int height);
    }

    public interface OnLayoutChangListener {
        void onChange(boolean changed, int left, int top, int right, int bottom);
    }

    public void setOnScreenSizeChangListener(OnScreenSizeChangListener onScreenSizeChangListener) {
        this.onScreenSizeChangListener = onScreenSizeChangListener;
    }

    public void setOnLayoutChangListener(OnLayoutChangListener onLayoutChangListener) {
        this.onLayoutChangListener = onLayoutChangListener;
    }
}
