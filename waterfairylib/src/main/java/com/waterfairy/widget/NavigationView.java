package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/25
 * @Description:
 */
public class NavigationView extends BaseView implements View.OnTouchListener {
    private int mNum = 3;
    private int mCurrentPos;
    private int mDWidth;//间隔宽
    private Paint mPaint;
    private int colorNormal, colorSelect;
    private float radio;

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDWidth = (int) (getDensity() * 30);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        colorSelect = Color.parseColor("#3cc676");
        colorNormal = Color.parseColor("#cdcdcd");
    }

    public void setColor(int colorNormal, int colorSelect) {
        this.colorSelect = colorSelect;
        this.colorNormal = colorNormal;
        invalidate();
    }

    public void setDWidth(int dWidth) {
        this.mDWidth = dWidth;
        invalidate();
    }

    public void setNum(int num) {
        this.mNum = num;
        mCurrentPos = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mNum == 0) return;
        int radius = height / 2;
        int startX = (width - (mNum * height) - (mNum - 1) * mDWidth) / 2;
        mPaint.setColor(colorNormal);
        for (int i = 0; i < mNum; i++) {
            canvas.drawCircle(startX + getCenterX(radius, i), radius, radius, mPaint);
        }
        mPaint.setColor(colorSelect);
        canvas.drawCircle(startX + getCenterX(radius, mCurrentPos) + radio * (mDWidth + height), radius, getChangeRadius(radius, radio), mPaint);
    }

    private float getChangeRadius(int radius, float radio) {
        return (float) (radius * (0.3 + (Math.abs(Math.abs(radio) - 0.5F) / 0.5f) * 0.7F));
    }

    private int getCenterX(int radius, int pos) {
        return (2 * pos + 1) * radius + mDWidth * pos;
    }

    public void setRadio(int position, float radio) {
        mCurrentPos = position;
        if (radio > 1) radio = 1;
        else if (radio < -1) radio = -1;
        this.radio = radio;
        invalidate();
    }


    private int moveStartX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            setRadio(mCurrentPos, (event.getX() - moveStartX) / (width / 4F));
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            moveStartX = (int) event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (radio > 0.5) {
                if (mCurrentPos < mNum - 1)
                    mCurrentPos++;
            } else if (radio < -0.5) {
                if (mCurrentPos > 0)
                    mCurrentPos--;
            }
            setRadio(mCurrentPos, 0);
        }
        return true;
    }

    public void setCurrentPos(int position) {
        mCurrentPos = position;
        invalidate();
    }
}
