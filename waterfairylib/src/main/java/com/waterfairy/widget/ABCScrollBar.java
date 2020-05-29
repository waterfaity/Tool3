package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseSelfView;

import java.util.HashMap;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/1/30
 * @Description:
 */

public class ABCScrollBar extends BaseSelfView {
    private int mTextSize;
    private int mTextColor;
    private int mBGColor;
    private Paint bgPaint, textPaint;
    private int selectPos = -1;
    private int lastPos = -1;
    private OnSelectLetterListener onSelectListener;
    private String selectLetter;
    private HashMap<String, Integer> letterPos;

    public ABCScrollBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        letterPos = new HashMap<>();
        onInitData();
    }

    public ABCScrollBar(Context context) {
        this(context, null);
    }

    @Override
    protected void beforeDraw() {
        char aChar = 'A';
        for (int i = 0; i < 27; i++) {
            String litter = Character.toString((char) (i == 26 ? aChar = '*' : (aChar + i)));
            letterPos.put(litter, i);
        }
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        mBGColor = Color.parseColor("#66666666");
        bgPaint.setColor(mBGColor);
        mTextSize = (int) (mWidth * 3 / 4F);
        if ((mTextSize + mTextSize / 4F) * 27F > mHeight) {
            mTextSize = (int) (mHeight / 27F * 4 / 5);
        }
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        mTextColor = Color.parseColor("#454545");
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(mTextSize);
    }

    @Override
    protected void drawOne(Canvas canvas) {
        canvas.drawArc(new RectF(0, 0, mWidth, mWidth), 180, 180, true, bgPaint);
        canvas.drawArc(new RectF(0, mHeight - mWidth, mWidth, mHeight), 0, 180, true, bgPaint);
        canvas.drawRect(0, mWidth / 2, mWidth, mHeight - mWidth / 2, bgPaint);

        char aChar = 'A';
        for (int i = 0; i < 27; i++) {
            String litter = Character.toString((char) (i == 26 ? aChar = '*' : (aChar + i)));
            if (i == 26) {
                aChar = '*';
            }
            int textSize = 0;
            if (selectPos == i) {
                //选中
                textPaint.setColor(Color.parseColor("#299ee1"));
                textSize = (int) (mTextSize * 5 / 4F);
            } else {
                textPaint.setColor(Color.parseColor("#454545"));
                textSize = mTextSize;
            }
            textPaint.setTextSize(textSize);
            int left = (int) ((mWidth - getTextLen(litter, textSize)) / 2);
            canvas.drawText(litter, left, (i + 1) * (mTextSize * 5 / 4F), textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                move(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                lastPos = -2;
                break;
        }
        return true;
    }

    private void move(float y) {
        if (y > 0) {
            selectPos = (int) (y / (mTextSize * 5 / 4F));
            if (selectPos != lastPos) {
                lastPos = selectPos;
                invalidate();
                if (onSelectListener != null) {
                    String letter = "";
                    if (selectPos == 26) {
                        letter = "*";
                    } else {
                        letter = Character.toString((char) ('A' + selectPos));
                    }
                    onSelectListener.onSelectLetter(selectPos, letter);
                }
            }
        }
    }

    public void setOnSelectListener(OnSelectLetterListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public void setSelect(int select) {
        if (select == selectPos) return;
        this.selectPos = select;
        invalidate();
    }

    public void setSelectLetter(String selectLetter) {
        if (TextUtils.equals(this.selectLetter, selectLetter)) return;
        this.selectLetter = selectLetter;
        Integer integer = letterPos.get(selectLetter);
        if (integer == null)
            selectPos = -1;
        else selectPos = integer;
        invalidate();
    }

    public interface OnSelectLetterListener {
        void onSelectLetter(int pos, String letter);
    }
}



