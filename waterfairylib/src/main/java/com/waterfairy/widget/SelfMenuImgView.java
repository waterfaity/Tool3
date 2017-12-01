package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.widget.baseView.BaseSelfView;
import com.waterfairy.widget.baseView.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by water_fairy on 2017/6/13.
 * 995637517@qq.com
 */

public class SelfMenuImgView extends BaseSelfView implements View.OnTouchListener {
    private int color, pressedColor;
    private Paint paint;
    private float radius;
    private List<Coordinate> list;
    private boolean onTouch;
    private OnClickListener onClickListener;

    public SelfMenuImgView(Context context) {
        super(context);
    }

    public SelfMenuImgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public void initData(int color, int pressedColor) {
        this.color = color;
        this.pressedColor = pressedColor;
        onInitData();
    }

    @Override
    protected void beforeDraw() {
        paint = new Paint();
        paint.setAntiAlias(true);
        list = new ArrayList<>();
        radius = 5 / 66f * mHeight;
        float baseHeight = 9 / 33f * mHeight;
        float differHeight = 15 / 66f * mHeight;
        float halfWidth = mWidth / 2f;
        for (int i = 0; i < 3; i++) {
            Coordinate coordinate = new Coordinate(halfWidth, baseHeight + differHeight * i);
            list.add(coordinate);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (paint == null) return;
        if (onTouch) paint.setColor(pressedColor);
        else paint.setColor(color);
        for (int i = 0; i < list.size() && list != null; i++) {
            Coordinate coordinate = list.get(i);
            canvas.drawCircle(coordinate.getX(), coordinate.getY(), radius, paint);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onTouch = true;
            postInvalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onTouch = false;
            postInvalidate();
            if (onClickListener != null) onClickListener.onClick(this);
        }
        return true;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }
}
