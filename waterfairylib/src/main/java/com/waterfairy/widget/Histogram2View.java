package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.widget.baseView.BaseSurfaceView;
import com.waterfairy.widget.baseView.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by water_fiay on 2017/6/24.
 * 995637517@qq.com
 */

public class Histogram2View extends BaseSurfaceView implements View.OnTouchListener {
    private List<HistogramEntity> mDataList;
    private List<Coordinate> mCoordinateList;
    private Coordinate mYTriangleCoordinate, mXTriangleCoordinate;//y轴三角形顶点,x轴三角形有顶点
    private Coordinate mYTitleCoordinate, mXTitleCoordinate;//xy坐标文字坐标
    private float mYGraduationRight, mXGraduationBottom;//xy坐标文字坐标
    private Coordinate mYTopContentCoordinate, mXRightContentCoordinate;//折线图左上角点坐标
    private Coordinate mZeroCoordinate, mZeroForXY;//零点坐标 xy轴交汇点
    private String mXTitle, mYTitle;//xy文字
    private int mTextColor, mXColor, mLineColor, mCircleColor, mXLineColor;//颜色
    private Paint mTextPaint, mXPaint, mLinePaint, mCirclePointPaint, mShadowPaint, mXLinePaint;//paint
    private float mTextSize;//文字大小
    private float mTriangleWidth;
    private int mXNum = 7;//x轴数量 默认7个
    private int mYNum = 4;//y轴坐标分段 如 5个坐标 4段
    private float mPerWidth;//x轴平均宽度(/mXNum)
    private float mPerHeight, mPerYValue;//y轴平均高度(/mYNum-1  /y轴最大值)
    private Path mXPath, mYPath, mLinePath, mShadowPath;
    private int mLineStrokeWidth, mXLineStrokeWidth;//边缘线宽度
    private int mCirclePointRadius;//圆点半径
    private int mShadowDownColor, mShadowUpColor;//过度颜色
    private OnTouchListener onTouchListener;
    private boolean mZeroCenter;
    private int tag;


    public Histogram2View(Context context) {
        super(context);
    }

    public Histogram2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLineStrokeWidth = (int) (context.getResources().getDisplayMetrics().density * 1);
        mXLineStrokeWidth = mLineStrokeWidth * 2;
        mTextSize = context.getResources().getDisplayMetrics().density * 12;
        mTextColor = Color.parseColor("#ff0000");
        mXLineColor = mTextColor;
        mXColor = Color.parseColor("#ff00ff");
        mLineColor = Color.parseColor("#00ffff");
        mCirclePointRadius = mLineStrokeWidth * 2;
        mCircleColor = mLineColor;
        mShadowUpColor = Color.parseColor("#00FF00");
        mShadowDownColor = Color.parseColor("#0000FF00");
        setOnTouchListener(this);
    }

    public void initLineStrokeWidth(int strokeWidth) {
        mLineStrokeWidth = strokeWidth;
    }

    public void initXLineStrokeWidth(int strokeWidth) {
        mXLineStrokeWidth = strokeWidth;
    }

    public void initColor(int textColor, int xColor, int lineColor) {
        this.mXLineColor = textColor;
        this.mTextColor = textColor;
        this.mXColor = xColor;
        this.mLineColor = lineColor;

    }

    public void initCirclePointData(int color, int radius) {
        this.mCircleColor = color == 0 ? mLineColor : color;
        this.mCirclePointRadius = radius == 0 ? mLineStrokeWidth * 2 : radius;
    }

    public void initTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public void initTitle(String xTitle, String yTitle) {
        this.mXTitle = xTitle;
        this.mYTitle = yTitle;
    }

    public void initData(List<HistogramEntity> dataList) {
        mDataList = dataList;
        if (mDataList == null) {
            mXNum = 1;
        } else {
            mXNum = mDataList.size();
        }
        super.onInitDataOk();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mXLinePaint = new Paint();
        mXLinePaint.setAntiAlias(true);
        mXLinePaint.setColor(mXLineColor);
        mXLinePaint.setStrokeWidth(mXLineStrokeWidth);

        mXPaint = new Paint();
        mXPaint.setStrokeWidth(2);
        mXPaint.setColor(mXColor);
        mXPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mCirclePointPaint = new Paint();
        mCirclePointPaint.setColor(mLineColor);
        mCirclePointPaint.setAntiAlias(true);
        mCirclePointPaint.setStyle(Paint.Style.FILL);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        Shader shader = new LinearGradient(0, 0, 0, mHeight, mShadowUpColor, mShadowDownColor, Shader.TileMode.REPEAT);
        mShadowPaint.setShader(shader);
    }

    public void initShadowColor(int upColor, int downColor) {
        this.mShadowUpColor = upColor;
        this.mShadowDownColor = downColor;
    }

    @Override
    protected void beforeDraw() {
        initPaint();
        initCoordinate();
        initXYData();
        initPath();
    }

    private void initCoordinate() {
        //y轴三角形顶点,x轴三角形有顶点
        mYTriangleCoordinate = new Coordinate(2 * mTextSize, 2 * mTextSize);
        mXTriangleCoordinate = new Coordinate(mWidth - 2 * mTextSize, mHeight - mTextSize * 2);
        //xy坐标文字坐标
        mYTitleCoordinate = new Coordinate(3f * mTextSize, mTextSize * 2);
        mXTitleCoordinate = new Coordinate(mWidth - 3 * mTextSize, mHeight - 3 * mTextSize);
        //折线图左上角点坐标
        mYTopContentCoordinate = new Coordinate(2 * mTextSize, 4 * mTextSize);
        mXRightContentCoordinate = new Coordinate(mWidth - 4 * mTextSize, mHeight - 3 * mTextSize);
        //mZeroCoordinate;//零点坐标
        mZeroCoordinate = new Coordinate(2 * mTextSize, mHeight - 3 * mTextSize);
        mZeroForXY = new Coordinate(2 * mTextSize, mHeight - 2 * mTextSize);

        mYGraduationRight = 1.5f * mTextSize;
        mXGraduationBottom = mHeight - 0.5f * mTextSize;

    }

    private void initXYData() {
        mTriangleWidth = mTextSize * 5 / 7;
        mPerWidth = (mXRightContentCoordinate.x - mZeroCoordinate.x) / (mXNum - 1);
        int maxValue = 0;
        for (int i = 0; mDataList != null && i < mDataList.size(); i++) {
            int value = mDataList.get(i).getValue();
            if (maxValue < value) maxValue = value;
        }
        calcPerY(maxValue);
    }

    private void calcPerY(int maxValue) {// 323   91
        float maxHeight = mZeroCoordinate.y - mYTopContentCoordinate.y;//最大高度差 (0-maxValue)
        if (maxValue == 0) {
            mZeroCenter = true;
            mZeroCoordinate.y = mZeroCoordinate.y - maxHeight / 2;
        } else {
            mZeroCenter = false;
            if (maxValue < mYNum) {//最大值 3  num = 4
                mYNum = maxValue;
                mPerHeight = maxHeight / maxValue;
                mPerYValue = maxValue / mYNum;
            } else {//最大值 4-> 更大   num = 4
                int i = maxValue / mYNum;//分数分成 四份
                for (int j = 0; j < mYNum; j++) {
                    int temp = maxValue + j;
                    if (temp % mYNum == 0) {
                        mPerYValue = temp / mYNum;
                        mPerHeight = maxHeight / temp;
                        break;
                    }
                }
            }
        }
    }

    private void initPath() {
        float temp1 = (float) (Math.sqrt(3) / 2 * mTextSize);
        float temp2 = mTextSize / 2;
        //y轴箭头
        mYPath = new Path();
        mYPath.moveTo(mYTriangleCoordinate.x, mYTriangleCoordinate.y - temp1);
        mYPath.lineTo(mYTriangleCoordinate.x - temp2, mYTriangleCoordinate.y);
        mYPath.lineTo(mYTriangleCoordinate.x + temp2, mYTriangleCoordinate.y);
        mYPath.lineTo(mYTriangleCoordinate.x, mYTriangleCoordinate.y - temp1);

        //x轴箭头
        mXPath = new Path();
        mXPath.moveTo(mXTriangleCoordinate.x, mXTriangleCoordinate.y + temp2);
        mXPath.lineTo(mXTriangleCoordinate.x, mXTriangleCoordinate.y - temp2);
        mXPath.lineTo(mXTriangleCoordinate.x + temp1, mXTriangleCoordinate.y);
        mXPath.lineTo(mXTriangleCoordinate.x, mXTriangleCoordinate.y + temp2);

        //折线 及 点
        mCoordinateList = new ArrayList<>();
        mLinePath = new Path();
        mLinePath.close();
        float startY = 0, endX = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            float x = mZeroCoordinate.x + i * mPerWidth;
            float y = mZeroCoordinate.y - mDataList.get(i).getValue() * mPerHeight;
            mCoordinateList.add(new Coordinate(x, y));
            if (i == 0) {
                mLinePath.moveTo(x, y);
                startY = y;
            } else mLinePath.lineTo(x, y);
            if (i == mDataList.size() - 1) {
                endX = x;
            }
        }
        //过度区域
        mShadowPath = new Path(mLinePath);
        mShadowPath.lineTo(endX, mZeroForXY.y);
        mShadowPath.lineTo(mZeroForXY.x, mZeroForXY.y);
        mShadowPath.lineTo(mZeroForXY.x, startY);

    }


    @Override
    protected void startDraw() {
        setClockNo();
    }

    @Override
    protected void drawOne(Canvas canvas) {
        drawSteady(canvas);
    }

    private void drawSteady(Canvas canvas) {
        canvas.drawColor(mBgColor);
        //x轴
        canvas.drawLine(mZeroForXY.x, mZeroForXY.y,
                mXTriangleCoordinate.x, mXTriangleCoordinate.y, mXLinePaint);
        canvas.drawText(mXTitle, mXTitleCoordinate.x, mXTitleCoordinate.y, mTextPaint);
        canvas.drawPath(mXPath, mXPaint);
        for (int i = 0; i < mDataList.size(); i++) {
            String text = mDataList.get(i).getxName();
            canvas.drawText(text, i * mPerWidth + mZeroCoordinate.x - getTextLen(text, mTextSize) / 2, mXGraduationBottom, mTextPaint);
        }


        //y轴
        canvas.drawLine(mZeroForXY.x, mZeroForXY.y,
                mYTriangleCoordinate.x, mYTriangleCoordinate.y, mXLinePaint);
        canvas.drawText(mYTitle, mYTitleCoordinate.x, mYTitleCoordinate.y, mTextPaint);
        canvas.drawPath(mYPath, mXPaint);
        int yNum = mYNum + 1;
        if (mZeroCenter) yNum = 1;
        for (int i = 0; i < yNum; i++) {
            float temp = (mPerYValue * i);
            float x = mYGraduationRight - getTextLen((int) temp + "", mTextSize);
            float y = mZeroCoordinate.y - mPerHeight * temp;
            canvas.drawText((int) temp + "", x, y, mTextPaint);
            //画y轴坐标线
//            canvas.drawLine(mZeroCoordinate.x, y, mXRightContentCoordinate.x, y, mXPaint);
        }

        canvas.drawPath(mLinePath, mLinePaint);
        //画折线上的数据值
        for (int i = 0; mCoordinateList != null & i < mCoordinateList.size(); i++) {
            Coordinate coordinate = mCoordinateList.get(i);
            canvas.drawCircle(coordinate.x, coordinate.y, mCirclePointRadius, mCirclePointPaint);
            String value = mDataList.get(i).getValue() + "";
            float textLen = getTextLen(value, mTextSize);
            float textX = coordinate.x - textLen / 2;
            float textY = coordinate.y - mTextSize;

            if (i == 0) {
                textX += (mTextSize);

            }
            canvas.drawText(value, textX, textY, mTextPaint);
        }

        canvas.drawPath(mShadowPath, mShadowPaint);


    }

    @Override
    protected void drawFinishView(Canvas canvas) {
        drawSteady(canvas);
    }

    public void setOnTouch2Listener(OnTouchListener onTouch2Listener) {
        this.onTouchListener = onTouch2Listener;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                if (onClickHistogramListener != null && !isDrawing)
                    handlerTouch(event.getX());
                break;

        }

        return onTouchListener == null || onTouchListener.onTouch(v, event);
    }

    private void handlerTouch(float upX) {
        for (int i = 0; i < mXNum; i++) {
            float tempX = mZeroForXY.x + i * mPerWidth + mPerWidth / 2;
            if (upX < mZeroForXY.x || upX > mXRightContentCoordinate.x) break;
            if (upX > mZeroForXY.x && upX < tempX) {
                onClickHistogramListener.onClickHistogram(i, (int) upX, tag);
                break;
            }
        }
    }

    private OnClickHistogramListener onClickHistogramListener;

    public void setOnClickHistogramListener(OnClickHistogramListener onClickHistogramListener) {
        this.onClickHistogramListener = onClickHistogramListener;
    }

    public void initBgColor(int bgColor) {
        this.mBgColor = bgColor;
    }

    public interface OnClickHistogramListener {
        /**
         * 点击的pos  以及点击的位置
         *
         * @param pos
         * @param x
         */
        void onClickHistogram(int pos, int x, int tag);
    }

    public void setTag(int tag) {
        this.tag = tag;

    }
}
