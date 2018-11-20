package com.waterfairy.widget.chart.ringChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.widget.baseView.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/9
 * @Description:
 */

public class RingChartView extends BaseView implements View.OnTouchListener {
    private String TAG = "RingChartView";
    //part text
    private int mPartTextColor;
    private float mPartTextSize;
    private int mRatioTextColor;
    private float mRatioTextSize;
    //center
    private float mTitleTextSize;
    private int mTitleTextColor;
    private String title = "总数";
    private float totalNumTextSize;
    private int totalNumColor;

    //暂时未用到
    private float mRatio = 1;//系数
    //数据
    private List<RingChartEntity> mDataList;
    //所有的实际值
    private List<Float> mValues;
    //总实际值
    private float mTotalValue;
    //设置显示的总值
    private int totalValueSet;
    //开始角度
    private List<Float> mStartDegree;
    private int mSelectPos = -1;
    //模块paint 字体paint
    private Paint mPaint, mTextPaint;
    //外环 内环边界
    private RectF mRectFInside;
    private RectF mRectFOutside;
    //选中的模块边界
    private RectF mTempRectFSelectInside;
    private RectF mTempRectFSelectOutside;
    //中心xy
    private float centerX, centerY;
    //每个模块的字体中心点
    private float textCenterX, textCenterY;
    //颜色列表
    private int[] colorList;
    //宽度分成的分数  20份
    private float mPerWidthValue;

    private boolean userCenter = false;
    private boolean setPartText = false;
    private OnPartSelectListener onPartSelectListener;
    private boolean showSetTotalValue;

    public RingChartView(Context context) {
        super(context);
    }

    public RingChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        initColor();
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);
        setOnTouchListener(this);
    }

    private void initData() {
        setPartTextColor(Color.parseColor("#99000000"), Color.parseColor("#99000000"));
        setPartTextSize(dp2Px(9), dp2Px(8));
        setCenterText("总数");
        setCenterText(dp2Px(12), Color.parseColor("#717171"));
        setTotalNumText(dp2Px(16), Color.parseColor("#7e7e7e"));
        mPaint = new Paint();
        mTextPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
    }

    private void initColor() {
        colorList = new int[]{
                Color.parseColor("#f49584"),
                Color.parseColor("#d15c83"),
                Color.parseColor("#c7985b"),
                Color.parseColor("#ded86a"),
                Color.parseColor("#8ee63b"),
                Color.parseColor("#44c4ea"),
                Color.parseColor("#7b72f1"),
                Color.parseColor("#45ac88"),
                Color.parseColor("#32b1bc"),
                Color.parseColor("#36b3e4"),
                Color.parseColor("#6737F9"),
                Color.parseColor("#f4558b"),
                Color.parseColor("#c44fe1"),
                Color.parseColor("#6eeadc"),
                Color.parseColor("#5ccfa9")
        };

    }

    public void setUserCenter(boolean userCenter) {
        this.userCenter = userCenter;
    }

    public boolean isSetPartText() {
        return setPartText;
    }

    public void setSetPartText(boolean setPartText) {
        this.setPartText = setPartText;
    }

    /**
     * 设置 每个模块字体的颜色
     *
     * @param partTextColor
     * @param ratioTextColor
     */
    public void setPartTextColor(int partTextColor, int ratioTextColor) {
        mPartTextColor = partTextColor;
        mRatioTextColor = ratioTextColor;
    }

    /**
     * 设置 每个模块的字体大小
     *
     * @param partTextSize
     * @param ratioTextSize
     */
    public void setPartTextSize(float partTextSize, float ratioTextSize) {
        mPartTextSize = partTextSize;
        mRatioTextSize = ratioTextSize;
    }

    /**
     * title 相关
     *
     * @param title
     */
    public void setCenterText(String title) {
        this.title = title;
    }

    /**
     * title 相关
     *
     * @param titleTextSize
     * @param titleTextColor
     */
    public void setCenterText(float titleTextSize, int titleTextColor) {
        mTitleTextSize = titleTextSize;
        this.mTitleTextColor = titleTextColor;
    }


    public void setTotalNumText(float titleNumTextSize, int totalNumColor) {
        this.totalNumTextSize = titleNumTextSize;
        this.totalNumColor = totalNumColor;
    }

    /**
     * 设置data
     *
     * @param dataList
     */
    public void setData(List<RingChartEntity> dataList) {
        this.mDataList = dataList;
        mValues = new ArrayList<>();
        mStartDegree = new ArrayList<>();
        mTotalValue = 0;
        if (mDataList != null && mDataList.size() > 0) {
            //计算总数值
            for (int i = 0; i < mDataList.size(); i++) {
                mTotalValue += mDataList.get(i).getValue();
            }
            //计算占比
            float tempValues = 0;
            for (int i = 0; i < mDataList.size(); i++) {
                RingChartEntity ringChartEntity = mDataList.get(i);
                float value = ringChartEntity.getValue();
                if (i == mDataList.size() - 1) {
                    //保证占比为1
                    mValues.add(1 - tempValues);
                    mStartDegree.add(tempValues * 360);
                    mStartDegree.add(360F);
                } else {
                    float tempRatio = 0;
                    if (mTotalValue != 0) {
                        tempRatio = value / mTotalValue;
                    }
                    mValues.add(tempRatio);
                    mStartDegree.add(tempValues * 360);
                    tempValues += tempRatio;
                }
            }
        } else {
            mTotalValue = 0;
        }
        invalidate();
    }

    public void setColorList(int[] colorList) {
        this.colorList = colorList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0 || mValues == null) {
            drawNull(canvas);
            return;
        }
        mPaint.reset();
        mPaint.setAntiAlias(true);
        //绘制图形
        for (int i = 0; i < mValues.size(); i++) {
            float angle = 360 * mValues.get(i) * mRatio;
            if (mSelectPos == i) {
                calcSelectRect(mStartDegree.get(i), angle);
                drawPart(canvas, i, mStartDegree.get(i), angle, mTempRectFSelectInside, mTempRectFSelectOutside, true);
            } else {
                drawPart(canvas, i, mStartDegree.get(i), angle, mRectFInside, mRectFOutside, false);
            }
        }
        if (!userCenter) {
            //画内圈
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(mRectFInside, 0, 360, true, mPaint);
            //画阴影环
            mPaint.setColor(Color.parseColor("#44FFFFFF"));
            mPaint.setStrokeWidth(mPerWidthValue / 2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, (mRectFInside.right - mRectFInside.left) / 2 + mPerWidthValue / 4, mPaint);
        }


        if (setPartText) {
            //绘制文本
            for (int i = 0; i < mValues.size(); i++) {
                float angle = 360 * mValues.get(i) * mRatio;
                if (mStartDegree.get(i) <= 0) continue;
                if (mSelectPos == i) {
                    drawPartText(canvas, i, mStartDegree.get(i), angle, mTempRectFSelectInside, mTempRectFSelectOutside);
                } else {
                    drawPartText(canvas, i, mStartDegree.get(i), angle, mRectFInside, mRectFOutside);
                }
            }
        }
        if (mValues == null || mValues.size() == 0) {
            mPaint.setColor(Color.parseColor("#aaaaaa"));
            canvas.drawArc(mRectFOutside, 0, 360, true, mPaint);

        }

        //绘制总分
        String numStr = "";
        String titleStr = "";
        if (mSelectPos > -1 && mDataList != null && mDataList.size() > 0 && mDataList.size() > mSelectPos) {
            numStr = mDataList.get(mSelectPos).getValueStr() + "";
            titleStr = mDataList.get(mSelectPos).getTitle();
            if (TextUtils.isEmpty(titleStr)) titleStr = title;
        } else {
            titleStr = title;
            if (showSetTotalValue) {
                numStr = totalValueSet + "";
            } else {
                numStr = mTotalValue + "";
            }
        }
        //中心数据值
        Rect totalNumRect = getTextRect(numStr, (int) totalNumTextSize);
        mTextPaint.setTextSize(totalNumTextSize);
        mTextPaint.setColor(totalNumColor);
        canvas.drawText(numStr, centerX - totalNumRect.width() / 2, centerY, mTextPaint);
        //中心title
        Rect titleRect = getTextRect(titleStr, (int) mTitleTextSize);
        mTextPaint.setTextSize(mTitleTextSize);
        mTextPaint.setColor(mTitleTextColor);
        canvas.drawText(titleStr, centerX - titleRect.width() / 2, centerY + titleRect.height(), mTextPaint);
    }

    private void calcSelectRect(float startAngle, float angle) {
        float centerAngle = startAngle + angle / 2;
        double radians = Math.toRadians(centerAngle);
        float y = (float) Math.sin(radians) * mPerWidthValue / 2;
        float x = (float) Math.cos(radians) * mPerWidthValue / 2;
        //外圈
        mTempRectFSelectOutside = new RectF(mRectFOutside.left + x, mRectFOutside.top + y, mRectFOutside.right + x, mRectFOutside.bottom + y);
        //最里圈
        mTempRectFSelectInside = new RectF(mRectFInside.left + x, mRectFInside.top + y, mRectFInside.right + x, mRectFInside.bottom + y);
    }

    private void drawPartText(Canvas canvas, int pos, float startAngle, float sweepAngle, RectF rectFInside, RectF rectFOutside) {
        //计算文本中心坐标
        float centerX = (rectFOutside.left + rectFOutside.right) / 2;
        float centerY = (rectFOutside.top + rectFOutside.bottom) / 2;
        float halfRadio = (rectFOutside.right - rectFOutside.left) * 39 / 100;
        float radians = (float) Math.toRadians(startAngle + sweepAngle / 2);
        textCenterX = (float) (centerX + Math.cos(radians) * halfRadio);
        textCenterY = (float) (centerY + Math.sin(radians) * halfRadio);
        //设置文本颜色 大小
        //画文本title
        mTextPaint.setTextSize(mPartTextSize);
        mTextPaint.setColor(mPartTextColor);
        String title = mDataList.get(pos).getTitle();
        Rect textRect = getTextRect(title, (int) mPartTextSize);
        canvas.drawText(title, textCenterX - textRect.width() / 2, textCenterY - 6, mTextPaint);
        //画比例 %
        mTextPaint.setTextSize(mRatioTextSize);
        mTextPaint.setColor(mRatioTextColor);
        title = ((int) (mValues.get(pos) * 100) + "%");
        textRect = getTextRect(title, (int) mPartTextSize);
        canvas.drawText(title, textCenterX - textRect.width() / 2, textCenterY + textRect.height() + 6, mTextPaint);
    }

    private void drawPart(Canvas canvas, int pos, float startAngle, float sweepAngle, RectF rectFInside, RectF rectFOutside, boolean isSelect) {
        //画外圈
        mPaint.setColor(colorList[pos % colorList.length]);
        canvas.drawArc(rectFOutside, startAngle + 0.05f, sweepAngle - 0.1f, true, mPaint);
        if (isSelect & !userCenter) {
            //画内圈
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(rectFInside, startAngle - 1, sweepAngle + 2, true, mPaint);
        }
    }

    private void drawNull(Canvas canvas) {
        //画外圈
        mPaint.setColor(colorList[0]);
        canvas.drawCircle(centerX, centerY, centerX, mPaint);
        if (!userCenter) {
            //画内圈
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(mRectFInside, 0, 360, true, mPaint);
            //画阴影环
            mPaint.setColor(Color.parseColor("#44FFFFFF"));
            mPaint.setStrokeWidth(mPerWidthValue / 2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, (mRectFInside.right - mRectFInside.left) / 2 + mPerWidthValue / 4, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = width / 2F;
        centerY = height / 2F;
        mPerWidthValue = width / 20F;
        float temp = 0;
        //外圈
        mRectFOutside = new RectF(temp = mPerWidthValue, temp, width - temp, height - temp);
        //最里圈
        mRectFInside = new RectF(temp = mPerWidthValue * 5f, temp, width - temp, height - temp);
    }

    public void selectPos(int selectPos) {
        selectPos(selectPos, true);
    }

    public void selectPos(int pos, boolean hasListener) {
        if (pos != this.mSelectPos) {
            mSelectPos = pos;
        } else {
            if (pos == -1) return;
            mSelectPos = -1;
        }
        if (mDataList != null && mDataList.size() > 1)
            fresh();
        if (onPartSelectListener != null && hasListener) {
            if (mSelectPos == -1) {
                onPartSelectListener.onPartSelect(-1, null);
            } else if (mDataList != null && mDataList.size() > pos) {
                onPartSelectListener.onPartSelect(pos, mDataList.get(pos));
            }
        }
    }

    private void calcTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        //计算距离中心的半径
        double radio = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        //判断内圆-外圆之间
        if (radio < centerX && (userCenter || radio > mRectFInside.width() / 2)) {
            //计算角度
            //tan =
            float dy = y - centerY;
            float dx = x - centerX;
            double degrees = Math.toDegrees(Math.atan((dy) / dx));
            if (dx >= 0 && dy >= 0) {
                //第一象限
            } else if (dx < 0 && dy > 0) {
                //二
                degrees = 180 - Math.abs(degrees);
            } else if (dx < 0 && dy < 0) {
                //三
                degrees += 180;
            } else {
                //四
                degrees = 360 - Math.abs(degrees);
            }
            if (mValues != null && mStartDegree != null) {
                for (int i = 0; i < mValues.size(); i++) {
                    if (degrees < mStartDegree.get(i + 1)) {
                        selectPos(i);
                        return;
                    }
                }
            }
        } else if (radio < mRectFInside.width() / 2) {
            //中心圆
            selectPos(-1);
        }
    }

    @Override
    protected void fresh() {
        super.fresh();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        invalidate();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                calcTouch(event);
                break;
        }
        return true;
    }

    public void setOnPartSelectListener(OnPartSelectListener onPartSelectListener) {
        this.onPartSelectListener = onPartSelectListener;
    }

    public void setTotalNumText(int totalValue, boolean showSetTotalValue) {
        this.totalValueSet = totalValue;
        this.showSetTotalValue = showSetTotalValue;
    }


    public interface OnPartSelectListener {
        void onPartSelect(int pos, RingChartEntity ringChartEntity);
    }
}
