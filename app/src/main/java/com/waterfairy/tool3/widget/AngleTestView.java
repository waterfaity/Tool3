package com.waterfairy.tool3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.utils.AngleUtils;

public class AngleTestView extends View {

    private final String TAG = "angleTestView";
    private int centerX;
    private int centerY;

    public AngleTestView(Context context) {
        super(context);
    }

    public AngleTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            move(event);
        }
        return true;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(centerX, centerY, 10, paint);
    }

    private void move(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Float[] lineK = AngleUtils.getLineK(x, y, centerX, centerY);
//        Log.i(TAG, "move: "+lineK[0]+"  "+lineK[1]+"  "+lineK[2]);


        double angle = AngleUtils.getAngle(x, y, centerX, centerY, -90);

        int num = 6;
        float perAngle = 360F / num;
        for (int i = 0; i < num; i++) {
            float startAngle = i * perAngle;
            float endAngle = (i + 1) * perAngle;
            if (startAngle < angle && angle < endAngle) {
//                Log.i(TAG, "move: " + i);
            }
        }
        Log.i(TAG, "move: " + ((int) angle) + " x:" + x + "  y:" + y);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        centerX = (right - left) / 2;
        centerY = (bottom - top) / 2;
    }
}
