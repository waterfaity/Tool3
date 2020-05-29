package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.waterfairy.widget.baseView.BaseSelfView;


/**
 * Created by water_fairy on 2017/7/19.
 * 995637517@qq.com
 */

public class FiveView extends BaseSelfView {
    private static final String TAG = "FiveView";
    private int solidColor = Color.parseColor("#fdae2d");
    private int lineColor = Color.parseColor("#777777");
    private int cont = 5;
    private int startAngle = 180;
    private float perAngle1, perAngle2;
    private Path path1, path2;
    private boolean solid = true;
    private float mCenterX, mCenterY;
    private float mCenterLineLen;
    private Paint paint;
    private boolean concave;

    //    外点
//            x=Rcos(72°*k)  y=Rsin(72°*k)   k=0,1,2,3,4
//    内点
//            r=Rsin18°/sin36°
//    x=rcos(72°*k+36°)  y=rsin(72°*k+36°)   k=0,1,2,3,4
    public FiveView(Context context) {
        super(context);
    }

    public FiveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canDraw)
            drawStatic(canvas);
    }

    public void initData(int count, int startAngle, boolean solid, boolean concave) {
        startAngle += 180;
        if (count == 0) count = 5;
        this.cont = count;
        this.startAngle = startAngle;
        this.solid = solid;
        this.concave = concave;
        initpaint();
        onInitDataOk();
    }

    private void initpaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
        }
        if (solid) {
            paint.setColor(solidColor);
        } else {
            paint.setColor(lineColor);
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void beforeDraw() {
        initLen();
        initAngle();
    }

    private void initLen() {
        perAngle2 = 180f / cont;
        perAngle1 = 360f / cont;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mCenterLineLen = Math.min(mCenterX, mCenterY);
    }

    private void initAngle() {
        path1 = new Path();
        if (concave) {
            initConcaveAngle();
        } else {
            initUnConcaveAngle();
        }
    }

    private void initUnConcaveAngle() {
        float firstX = 0, firstY = 0;
        for (int i = 0; i < cont; i++) {
            float angle = startAngle + i * perAngle1;
            double v = Math.toRadians(angle);
            float x = 0, y = 0;
            x = (float) (mCenterX + Math.sin(v) * mCenterLineLen);
            y = (float) (mCenterY + Math.cos(v) * mCenterLineLen);
            if (i == 0) {
                firstX = x;
                firstY = y;
                path1.moveTo(x, y);
            } else {
                path1.lineTo(x, y);
            }
            if (i + 1 == cont && !solid) {
                path1.lineTo(firstX, firstY);
            }
        }
    }

    private void initConcaveAngle() {
        float firstX = 0, firstY = 0;
        for (int i = 0; i < cont * 2; i++) {
            boolean outPoint = i % 2 == 0;
            float angle = startAngle + i / 2 * perAngle1 + (outPoint ? 0 : perAngle2);
            double v = Math.toRadians(angle);
            float radius = outPoint ?
                    mCenterLineLen
                    : (float) (mCenterLineLen * (Math.sin(Math.toRadians(perAngle2 / 2f)) / Math.sin(Math.toRadians(perAngle1 / 2f))));
            float x = 0, y = 0;
            x = (float) (mCenterX + Math.sin(v) * radius);
            y = (float) (mCenterY + Math.cos(v) * radius);
            if (i == 0) {
                firstX = x;
                firstY = y;
                path1.moveTo(x, y);
            } else {
                path1.lineTo(x, y);
            }
            Log.i(TAG, "initConcaveAngle: " + i + "--:-- " + x + "--" + y);
            if (i + 1 == cont * 2) {
                path1.lineTo(firstX, firstY);
            }
        }
    }


    private void drawStatic(Canvas canvas) {
        canvas.drawPath(path1, paint);
    }
}
