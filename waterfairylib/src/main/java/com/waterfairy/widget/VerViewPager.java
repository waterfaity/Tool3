package com.waterfairy.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/6 16:22
 * @info:
 */
public class VerViewPager extends ViewPager {
    private int currentPosition;
    private boolean autoMove = false;
    private float scrollHeight;
    private int lastPosition;
    float startY = 0, startX;
    float endY = 0, endX;

    public VerViewPager(@NonNull Context context) {
        this(context, null);
    }

    public VerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scrollHeight = 3;
        setPageTransformer(true, new DefaultTransformer());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        handleMotionEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }


    private boolean handleMotionEvent(MotionEvent ev) {
        if (autoMove) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                endY = ev.getY();
                endX = ev.getX();
                handleEnd();
                break;
        }
        return true;
    }

    private void handleEnd() {
        float dy = endY - startY;
        float dx = endX - startX;
        if (Math.abs(dy) > Math.abs(dx)) {
            if (dy > scrollHeight) {
                //下滑
                moveTo(false);
            } else if (dy < -scrollHeight) {
                //上滑
                moveTo(true);
            }
        }
    }

    private void moveTo(boolean next) {
        if ((currentPosition == 0 && !next) || (currentPosition >= getAdapter().getCount() - 1 && next))
            return;//第一页 或 最后一页
        lastPosition = currentPosition;
        if (next) {
            currentPosition++;
        } else {
            currentPosition--;
        }
        setCurrentItem(currentPosition);
    }


    public class DefaultTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            float alpha = 0;
            if (0 <= position && position <= 1) {
                alpha = 1 - position;
            } else if (-1 < position && position < 0) {
                alpha = position + 1;
            }
            view.setAlpha(alpha);
            float transX = view.getWidth() * -position;
            view.setTranslationX(transX);
            float transY = position * view.getHeight();
            view.setTranslationY(transY);
        }
    }
}
