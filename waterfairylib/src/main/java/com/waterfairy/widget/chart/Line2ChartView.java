package com.waterfairy.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseView;
import com.waterfairy.widget.baseView.Coordinate;
import com.waterfairy.widget.baseView.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/8 13:45
 * @info:
 */
public class Line2ChartView extends BaseView {

    //传入数据
    private List<Entity> mDataList;//输入数据
    private int maxValue;
    //data
    private List<Coordinate> mCoordinateList;//数据坐标
    private List<Entity> mYLineList;//Y轴线坐标
    private int mWidthEntity;//间隔
    private int mTextSize;
    //坐标
    private int scrollX; //滚动距离
    private int maxScrollX;
    private int bottomLine;//底部y坐标
    private int leftLine, rightLine;//左侧x坐标
    private int topLine;
    private int yNum = 4;//y轴个数
    private int xMaxNum = 10;//x轴最大个数
    private int xNum;//x轴个数
    private int startPos = 0;//x轴开始绘制位置
    private int perWidth;//x间隔
    private int dNumDrawXText = 1;//每n个画x轴坐标

    private int textWidth, textHeight;
    private float perValueHeight;
    private int radius;
    //画笔
    private Paint mPaintLineY;
    private Paint mPaintText;
    private Paint mPaintLine;
    private boolean parentDisTouch = false;

    public Line2ChartView(Context context) {
        this(context, null);
    }

    public Line2ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getDensity();
        initText();
        initPaint();
    }

    /**
     * 计算文本相关
     */
    private void initText() {
        mTextSize = (int) (getDensity() * 12);
        Rect textRect = getTextRect("0", mTextSize);
        textHeight = Math.abs(textRect.bottom + textRect.top);
        textWidth = textRect.right + textRect.left;
    }

    private void initPaint() {
        mPaintLineY = new Paint();
        mPaintLineY.setColor(Color.parseColor("#999999"));
        mPaintLineY.setStrokeWidth(density);

        mPaintText = new Paint();
        mPaintText.setColor(Color.parseColor("#454545"));
        mPaintText.setTextSize(mTextSize);
        mPaintText.setAntiAlias(true);

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#289456"));
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(density * 1);


    }

    public void iniLineColor(int color) {
        mPaintLine.setColor(color);
    }

    public List<Entity> getDataList() {
        return mDataList;
    }

    public void setData(List<Entity> mDataList, int maxValue) {
        this.maxValue = maxValue;
        this.mDataList = mDataList;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
        if (width != 0 && height != 0) {
            calcData();
        }
    }

    /**
     * 计算数据坐标
     */
    private void calcData() {
        calcNormal();
        //计算y轴
        calcY();
        //计算x轴
        calcX();
    }


    private void calcNormal() {
        if (yNum < 1) yNum = 1;
        if (maxValue <= 0) maxValue = 100;
        rightLine = width - textWidth * 2 - getPaddingRight();
        leftLine = (("" + maxValue).length() + 2) * textWidth + getPaddingLeft();
        radius = (int) (density * 2.5);
    }

    private void calcY() {
        //计算y轴坐标 上留 一个字符高度  下留2个字符高度
        for (int i = 0; i < yNum; i++) {
            if ((maxValue + i) % yNum == 0) {
                int tempMaxValue = maxValue + i;
                //设置底部线
                bottomLine = height - 3 * textHeight - getPaddingBottom();
                topLine = textHeight * 2 + getPaddingTop();
                int perHeight = (bottomLine - topLine) / yNum;
                perValueHeight = (bottomLine - topLine) / (float) tempMaxValue;
                mYLineList = new ArrayList<>();
                for (int j = 0; j < yNum + 1; j++) {
                    Entity entity = new Entity();
                    //y坐标
                    entity.value = bottomLine - j * perHeight;
                    //y显示文字
                    entity.name = tempMaxValue / yNum * j + "";
                    mYLineList.add(entity);
                }
            }
        }
    }

    private void calcX() {
        if (mDataList != null && mDataList.size() > 0) {
            //计算坐标
            mCoordinateList = new ArrayList<>();
            int tempLineWidth = rightLine - leftLine;
            xNum = mDataList.size();
            if (xNum > xMaxNum) xNum = xMaxNum;
            if (xNum == 1) {
                //只有一个
                Entity entity = mDataList.get(0);
                mCoordinateList.add(new Coordinate(leftLine + (tempLineWidth / 2), (int) (bottomLine - entity.getValue() * perValueHeight), entity.getValue(), entity.getName()));
            } else {
                perWidth = tempLineWidth / (xNum - 1);
                for (int i = 0; i < mDataList.size(); i++) {
                    Entity entity = mDataList.get(i);
                    mCoordinateList.add(new Coordinate(leftLine + i * perWidth, (int) (bottomLine - entity.getValue() * perValueHeight), entity.getValue(), entity.getName()));
                }
                maxScrollX = perWidth * (mCoordinateList.size() - xNum);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制y轴
        drawY(canvas);
        //绘制x轴
        drawX(canvas);
    }

    private void drawX(Canvas canvas) {
        if (mCoordinateList == null || mCoordinateList.size() == 0) return;
        int maxPos = startPos + xNum;
        if (dNumDrawXText < 0) dNumDrawXText = 1;
        for (int i = startPos; i < maxPos + 1 && i < mCoordinateList.size(); i++) {
            Coordinate coordinate = mCoordinateList.get(i);
            int x = (int) (coordinate.x + scrollX);
            boolean overSideLeft = false;
            boolean overSideRight = false;
            if (x < leftLine) overSideLeft = true;
            if (x > rightLine) overSideRight = true;
            if (!overSideLeft) {
                //没有超出左边界
                // 绘制x坐标刻度
                if (!overSideRight) {
                    canvas.drawLine(x, bottomLine, x, bottomLine + textHeight / 2, mPaintLineY);
                }
                //绘制圆点
                canvas.drawCircle(x, coordinate.y, radius, mPaintLine);
                //绘制数据值
                Rect textRect = getTextRect(coordinate.value + "", mTextSize);
                int widthHalf = textRect.width() / 2;
                canvas.drawText(coordinate.value + "", x - widthHalf, coordinate.y - textHeight, mPaintText);
                if (i != maxPos && (i + 1 < mCoordinateList.size())) {
                    //画折线
                    Coordinate coordinate2 = mCoordinateList.get(i + 1);
                    canvas.drawLine(x, coordinate.y, coordinate2.x + scrollX, coordinate2.y, mPaintLine);
                }
            }

            if (i % dNumDrawXText == 0 && !TextUtils.isEmpty(coordinate.text)) {
                Rect textRect = getTextRect(coordinate.text, mTextSize);
                int width = textRect.right + Math.abs(textRect.left);
                int height = Math.abs(textRect.bottom - textRect.top);
                canvas.drawText(coordinate.text, x - width / 2, bottomLine + height * 2F, mPaintText);
            }
        }
    }

    private void drawY(Canvas canvas) {
        if (mYLineList == null) return;
        for (int i = 0; i < mYLineList.size(); i++) {
            Entity entity = mYLineList.get(i);
            canvas.drawLine(leftLine, entity.getValue(), rightLine, entity.getValue(), mPaintLineY);
            canvas.drawText(entity.getName(), textWidth + getPaddingLeft(), entity.getValue() + textHeight / 2, mPaintText);
        }
    }


    public void setNumDrawXText(int dNumDrawXText) {
        this.dNumDrawXText = dNumDrawXText;
    }

    float lastMoveX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastMoveX = event.getX();
            getParent().requestDisallowInterceptTouchEvent(parentDisTouch);
        } else if (action == MotionEvent.ACTION_MOVE) {
            handleMove(event);
        }
        return true;
    }

    private void handleMove(MotionEvent event) {
        float dx = event.getX() - lastMoveX;
        if (dx == 0 || xNum <= 1) return;
        scrollX += (event.getX() - lastMoveX);
        if (scrollX > 0) scrollX = 0;
        else if (scrollX < -maxScrollX) scrollX = -maxScrollX;
        startPos = -scrollX / perWidth;
        if (startPos < 0) startPos = 0;
        lastMoveX = event.getX();
        invalidate();
    }

    public void setParentDisTouch(boolean parentDisTouch) {
        this.parentDisTouch = parentDisTouch;
    }

}
