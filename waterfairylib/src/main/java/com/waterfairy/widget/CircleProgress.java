package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.waterfairy.widget.baseView.BaseSelfView;
import com.waterfairy.widget.baseView.OnFloatChangeListener;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/5/7 09:42
 * @info: 进度条
 */
public class CircleProgress extends BaseSelfView {
    private float score;
    private Paint paint;
    private RectF rectF;
    private int maxAngle;
    private float centerX;
    private float centerY;
    private int[] colors;  //过度颜色
    private float[] points;  //过度位置
    private SweepGradient sweepGradient;//过度
    private float lineRadius;//线半径;
    private float realRadius;//半径

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        colors = new int[]{Color.parseColor("#f76f60"), Color.parseColor("#f47264"), Color.parseColor("#f8988a")};
        points = new float[]{0, 0.3F, 1F};

    }


    int angle = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(-90, centerX, centerY);
        //背景
        paint.setStrokeWidth(2 * lineRadius);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#fee7e5"));
        paint.setShader(null);
        canvas.drawArc(rectF, angle, 360 - angle, false, paint);
        //前景
        paint.setShader(sweepGradient);
        canvas.drawArc(rectF, 0, angle, false, paint);
        //画圆点
        paint.setShader(null);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        paint.setColor(Color.parseColor("#f76f60"));
        paint.setStrokeWidth(2 * lineRadius);
        canvas.drawPoint(centerX + realRadius, centerY, paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(lineRadius);
        canvas.drawPoint(centerX + realRadius, centerY, paint);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            int min = Math.min(w, h);
            float radius = min / 2F;
            lineRadius = radius / 16;
            realRadius = radius - lineRadius;
            centerX = w / 2F;
            centerY = h / 2F;
            rectF = new RectF(centerX - realRadius, centerY - realRadius, centerX + realRadius, centerY + realRadius);
            sweepGradient = new SweepGradient(centerX, centerY, colors, points);
        }
    }

    public void setScore(float score) {
        this.score = score;
        maxAngle = (int) (this.score / 100F * 360);
    }

    public void start() {
        setSleepTime(20);
        setTimes(100);
        setClock(new OnFloatChangeListener() {
            @Override
            public void onChange(float value) {
                angle = (int) (value * maxAngle);
                if (onProgressChangeListener != null) onProgressChangeListener.onProgress(value);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private OnProgressChangeListener onProgressChangeListener;

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public interface OnProgressChangeListener {
        void onProgress(float value);
    }
}
