package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.waterfairy.widget.baseView.BaseView;
import com.waterfairy.widget.utils.CanvasUtils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/8/8 14:53
 * @info:
 */
public class ProgressView extends BaseView {
    private int max = 100;
    private int progress;
    private RectF rectBG, rectProgress;
    private int colorBG, colorProgress;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        colorBG = Color.parseColor("#eeeeee");
        colorProgress = Color.parseColor("#FF0000");
    }

    @Override
    public void onViewMeasure(boolean changed, int width, int height) {
        super.onViewMeasure(changed, width, height);
        calc();
    }

    public void setColor(int colorProgress) {
        this.colorProgress = colorProgress;
        invalidate();
    }

    private void calc() {
        rectBG = new RectF(0, 0, getWidth(), getHeight());
        if (width > height) {
            rectProgress = new RectF(0, 0, (int) ((width - height) * (progress / (float) max) + height), height);
        } else {
            rectProgress = new RectF(0, 0, (int) (width * (progress / (float) max)), height);
        }
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setProgress(float radio) {
        this.max = 100;
        this.progress = (int) (radio * 100);
        invalidate();
    }


    public void setProgress(int max, int progress) {
        this.max = max;
        this.progress = progress;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CanvasUtils.drawCorner(canvas, rectBG, getHeight() / 2, 0, 0, colorBG, null);
        if (progress > 0) {
            CanvasUtils.drawCorner(canvas, rectProgress, getHeight() / 2, 0, 0, colorProgress, null);
        }
    }
}
