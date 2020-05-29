package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.waterfairy.widget.baseView.BaseSelfView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/9
 * @Description:
 */

public class PlayProgressView extends BaseSelfView {
    private int radius;
    private float radio;
    private Paint bgPaint;
    private Paint paint;
    private Paint paintCircle;
    private int strokeWidthCircle;
    private int color = Color.BLUE;

    public PlayProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        onInitDataOk();
    }

    public PlayProgressView(Context context) {
        this(context, null);


    }

    public void setProgress(int current, int total) {
        if (total == 0) {
            radio = 0;
        } else {
            radio = current / (float) total;
        }
        onInitDataOk();
    }

    @Override
    protected void onViewInitOk() {
        super.onViewInitOk();
        radius = mWidth / 2;
        strokeWidthCircle = mWidth / 14;
        bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#66FFFFFF"));
        bgPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(1.5f * strokeWidthCircle);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paintCircle = new Paint();
        paintCircle.setColor(color);
        paintCircle.setAntiAlias(true);
        paintCircle.setStrokeWidth(strokeWidthCircle);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void drawOne(Canvas canvas) {
        super.drawOne(canvas);
        canvas.drawCircle(radius, radius, radius - strokeWidthCircle / 2, bgPaint);
        RectF rectF = new RectF(0 + strokeWidthCircle / 2, 0 + strokeWidthCircle / 2, mWidth - +strokeWidthCircle / 2, mHeight - +strokeWidthCircle / 2);
        canvas.drawArc(rectF, -90, 360 * radio, false, paintCircle);
        canvas.drawLine(mWidth * 2 / 5, mHeight / 3, mWidth * 2 / 5, mHeight * 2 / 3, paint);
        canvas.drawLine(mWidth * 3 / 5, mHeight / 3, mWidth * 3 / 5, mHeight * 2 / 3, paint);
    }
}
