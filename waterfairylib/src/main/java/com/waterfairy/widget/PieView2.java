package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.widget.baseView.BaseSurfaceView;
import com.waterfairy.widget.baseView.OnCanvasChangeListener;

import java.util.ArrayList;
import java.util.List;

//import android.util.Log;

/**
 * Created by water_fairy on 2017/6/15.
 * 995637517@qq.com
 */

public class PieView2 extends BaseSurfaceView implements OnCanvasChangeListener, View.OnTouchListener {
    private final String TAG = "pieView";
    private List<Integer> mPercentRatios;//比例
    private List<Integer> mColors;//颜色
    private List<Float> mAngles;//角度
    private List<Float> mStartAngles;//对应开始角度
    private List<Paint> mPaints;//对应画笔

    private int mCount;//板块数量
    private int mRadius;//圆半径
    private int padding;//布局padding
    private RectF mCircleRectF;//圆区域
    private Thread drawThread;//绘画线程
    private boolean drawCircle;//是否画圆圈
    private int circleColor;//圆圈颜色
    private int circleStrokeWidth;//圆圈宽度
    private int startDegree;//默认水平为0
    private List<String> mStrings;//显示文本
    private List<Coordinate> mSectorCenters;//扇形中心点
    private List<Coordinate> mTextOutLines;//文字说明 坐标起点
    private List<Coordinate> mTextInLines;//文字说明 坐标结束点
    private List<Coordinate> mTextStarts;//文字说明起点
    private int mCenterX, mCenterY;
    private int mTextSize;
    private int mTextColor;
    private Paint mTextPaint, mPercentPaint;
    private Paint mLinePaint;
    private List<Float> mRatios;
    private int mBgColor;
    private int mTag;//作为标记

    public PieView2(Context context) {
        super(context);
    }

    public PieView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBgColor = Color.WHITE;
        mTextSize = (int) (context.getResources().getDisplayMetrics().density * 10);
        mTextColor = Color.GRAY;
        setOnTouchListener(this);
    }

    public void setSelfTag(int tag) {
        this.mTag = tag;
    }

    @Override
    protected void beforeDraw() {
        initBaseData();
        initStrokePoint();
    }

    @Override
    protected void startDraw() {
        setClock(this, 50);
//        setClockNo();
    }


    @Override
    public void onDrawChange(Canvas canvas, float value) throws Exception {
//        drawBg(canvas);
        startDrawArc(canvas, value);
        if (value == 1) drawInfo(canvas);
    }

    @Override
    protected void drawOne(Canvas canvas) {
        startDrawArc(canvas, 1);
        drawInfo(canvas);
    }

    @Override
    protected void drawFinishView(Canvas canvas) {
        startDrawArc(canvas, 1);
        drawInfo(canvas);
    }

    private void startDrawArc(Canvas canvas, float ratio) {
//        Log.i(TAG, "drPieView: " + ratio);
        if (canvas == null) return;
        canvas.drawColor(mBgColor);
        if (drawCircle) drawCircle(canvas);
        float sin = (float) Math.sin(Math.toRadians(ratio * 90));
        for (int i = 0; i < mCount; i++) {
            Paint paint = mPaints.get(i);
            float startAngle = mStartAngles.get(i);
            canvas.drawArc(mCircleRectF, startAngle, mAngles.get(i) * sin, true, paint);
        }
    }

    private void initBaseData() {
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
//        Log.i(TAG, "onMeasure: width:" + mWidth + " -height" + mHeight);
        int tempRadius = Math.min(mCenterX, mCenterY);
        mRadius = (int) (tempRadius * 7F / 14);
        padding = (int) (tempRadius * 2F / 14);
        if (mCircleRectF == null) {
            mCircleRectF = new RectF(
                    mCenterX - mRadius,
                    mCenterY - mRadius,
                    mCenterX + mRadius,
                    mCenterY + mRadius);
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isDrawing || !hasInitData || onPositionSelectedListener == null) return false;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            calcTouchPosition(event.getX(), event.getY());
        }
        return true;
    }

    private void calcTouchPosition(float x, float y) {
//        mCenterX,mCenterY,mRadius;
        float radius = (float) Math.sqrt(Math.pow(y - mCenterY, 2) + Math.pow(x - mCenterX, 2));
        if (radius > mRadius) return;
        double v = Math.toDegrees(Math.asin((y - mCenterY) / radius));
        double degree = 0;
        if (x > mCenterX) {
            //右侧
            if (v > 0) {
                //下
                degree = v;
            } else {
                //上
                degree = 360 + v;
            }
        } else {
            //左侧
            if (v > 0) {
                //下
                degree = 180 - v;
            } else {
                //上
                degree = 180 + Math.abs(v);
            }
        }
        int positionSel = 0;
        boolean hasData = false;
        for (int i = 0; i < mStartAngles.size(); i++) {
            Float aFloat = mStartAngles.get(i);
            if (degree < aFloat) {
                positionSel = i - 1;
                hasData = true;
                break;
            }
        }
        if (!hasData) {
            positionSel = mStartAngles.size() - 1;
        }
        if (onPositionSelectedListener != null)
            onPositionSelectedListener.onPieSelect(positionSel, mTag);

    }

    /**
     * 计算扇形边缘中心点
     */
    private void initStrokePoint() {
        mSectorCenters = new ArrayList<>();
        mTextOutLines = new ArrayList<>();
        mTextInLines = new ArrayList<>();
        mTextStarts = new ArrayList<>();

        for (int i = 0; i < mCount; i++) {
            float angle = mStartAngles.get(i) + mAngles.get(i) / 2;
            mSectorCenters.add(getStrokePoint(angle, mRadius));
            mTextInLines.add(getStrokePoint(angle, mRadius - padding));
            PieView2.Coordinate outTempPoint = getStrokePoint(angle, mRadius + padding);
            mTextOutLines.add(outTempPoint);
            mTextStarts.add(getTextStartPoint(mStrings.get(i), outTempPoint));
        }
    }

    /**
     * 计算文本起点
     *
     * @param content
     * @param outPoint
     * @return
     */
    private PieView2.Coordinate getTextStartPoint(String content, PieView2.Coordinate outPoint) {
        PieView2.Coordinate coordinate = new PieView2.Coordinate();
        int x = outPoint.getX();
        int y = outPoint.getY();
        int xTag = 0;
        if (x > mCenterX) xTag = 1;
        else if (x < mCenterX) xTag = -1;
        int yTag = 0;
        if (y > mCenterY) yTag = 1;
        else if (y < mCenterY) yTag = -1;
        coordinate.setX(x + xTag * getTextLen(content)).setY(y).setxTag(xTag).setyTag(yTag);
        return coordinate;
    }

    private int getTextLen(String content) {
        if (TextUtils.isEmpty(content)) {
            return 0;
        } else {
            return content.getBytes().length * (mTextSize / 3);
        }
    }

    public void initData(List<PieViewDataBean> dataBeanList) {
        if (dataBeanList != null) {
            List<Float> ratios = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            List<String> strings = new ArrayList<>();
            for (int i = 0; i < dataBeanList.size(); i++) {
                PieViewDataBean pieViewDataBean = dataBeanList.get(i);
                ratios.add(pieViewDataBean.getRatio());
                colors.add(pieViewDataBean.getColor());
                strings.add(pieViewDataBean.getName());
            }
            initData(ratios, colors, strings);
        }
    }

    public void initData(List<Float> ratios, List<Integer> colors, List<String> strings) {
        mAngles = new ArrayList<>();
        mStartAngles = new ArrayList<>();
        mPaints = new ArrayList<>();
        mSectorCenters = new ArrayList<>();
        mStrings = strings;
        mColors = colors;
        mCount = ratios.size();
        mRatios = ratios;
        initPaint();
        calcCircleData(ratios, colors);
        //计算文本所需数据
        onInitDataOk();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //文本
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);     //文本
        mPercentPaint = new Paint();
        mPercentPaint.setColor(mTextColor);
        mPercentPaint.setTextSize(mTextSize * 4 / 5);
        mPercentPaint.setAntiAlias(true);
        //线
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setTextSize(mTextSize);
        mLinePaint.setAntiAlias(true);
    }

    /**
     * 计算圆图所需数据
     *
     * @param ratios
     * @param colors
     */
    private void calcCircleData(List<Float> ratios, List<Integer> colors) {
        float total = 0;
        //总数
        for (int i = 0; i < mCount; i++) {
            total += ratios.get(i);
        }
        mPercentRatios = new ArrayList<>();
        //对应开始角度
        float startAngle = 0;
        float lastAngle = 0;
        for (int i = 0; i < mCount; i++) {
            //计算对应角度
            float ratio = ratios.get(i) / total;
            int tempNum = (int) (ratio * 100);
            if (tempNum == 0 && ratio != 0) tempNum = 1;
            mPercentRatios.add(tempNum);
            float angle = 360 * (ratio);
            mAngles.add(angle);//获取角度
            //计算对应开始角度
            startAngle += lastAngle;
            lastAngle = angle;
            float startTemp = startAngle + startDegree;
            mStartAngles.add(startTemp);

            //设置对应颜色
            Paint paint = new Paint();
            paint.setAntiAlias(true);//去锯齿
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colors.get(i));//颜色
            mPaints.add(paint);
        }
    }

    private PieView2.Coordinate getStrokePoint(float angle, int radius) {
//        Log.i(TAG, "getStrokePoint: " + angle);
        PieView2.Coordinate coordinate = new PieView2.Coordinate();
        int y = (int) (mCenterY + (Math.sin(Math.toRadians(angle)) * radius));
        int x = (int) ((Math.cos(Math.toRadians(angle)) * radius) + mCenterX);

        int xTag = 0;
        if (x > mCenterX) xTag = 1;
        else if (x < mCenterX) xTag = -1;

        int yTag = 0;
        if (y > mCenterY) yTag = 1;
        else if (y < mCenterY) yTag = -1;

        coordinate.setX(x).setY(y).setxTag(xTag).setyTag(yTag);
//        Log.i(TAG, "getStrokePoint: " + x + "--" + y);
        return coordinate;
    }

    private void drawInfo(Canvas canvas) {
        if (canvas == null) return;
        for (int i = 0; i < mCount; i++) {
            drawLines(canvas, i);
            drawTexts(canvas, i);
        }
    }

    private void drawTexts(Canvas canvas, int i) {
        int num = (int) Float.parseFloat(mRatios.get(i) + "");
        String temp = "(" + num + "本)";
        String content = mStrings.get(i);
        PieView2.Coordinate coordinate = mTextOutLines.get(i);
        PieView2.Coordinate startCoordinate = mTextStarts.get(i);
        int dex = mTextSize / 4;//调整差距
        int centerX = (coordinate.getX() + startCoordinate.getX()) / 2 - 2 * mTextSize;
        if (coordinate.getxTag() >= 0) {
            //右侧
            canvas.drawText(content, coordinate.getX() - mTextSize, coordinate.getY() - dex, mTextPaint);
//            canvas.drawText(mPercentRatios.get(i) + "%" + temp, centerX - mTextSize, coordinate.getY() + mTextSize, mPercentPaint);//67%
            canvas.drawText(temp, centerX - mTextSize, coordinate.getY() + mTextSize, mPercentPaint);//67%
        } else {
            if (startCoordinate.getyTag() > 0) {
                //左下
                canvas.drawText(content, startCoordinate.getX() + mTextSize, startCoordinate.getY() - dex, mTextPaint);
//                canvas.drawText(mPercentRatios.get(i) + "%" + temp, centerX + mTextSize, startCoordinate.getY() + mTextSize, mPercentPaint);//67%
                canvas.drawText(temp, centerX + mTextSize, startCoordinate.getY() + mTextSize, mPercentPaint);//67%
            } else {
                //左上
                canvas.drawText(content, startCoordinate.getX() + mTextSize, startCoordinate.getY() - dex, mTextPaint);
//                canvas.drawText(mPercentRatios.get(i) + "%" + temp, centerX + mTextSize, startCoordinate.getY() + mTextSize, mPercentPaint);//67%
                canvas.drawText(temp, centerX + mTextSize, startCoordinate.getY() + mTextSize, mPercentPaint);//67%
            }
        }
    }

    /**
     * 划线连接扇形
     *
     * @param canvas
     * @param i
     */
    private void drawLines(Canvas canvas, int i) {
        PieView2.Coordinate outLinePoint = mTextOutLines.get(i);
        PieView2.Coordinate inLinePoint = mTextInLines.get(i);
        PieView2.Coordinate textStartPoint = mTextStarts.get(i);
        int outX = outLinePoint.getX();
        int outY = outLinePoint.getY();
        canvas.drawLine(outX, outY, inLinePoint.getX(), inLinePoint.getY(), mLinePaint);//线1(斜线)
//        int textStartX = textStartPoint.getX();
        int textStartY = textStartPoint.getY();
        canvas.drawLine(textStartPoint.getxTag() > 0 ? outX + mTextSize : outX - mTextSize, textStartY, outX, outY, mLinePaint);//线2(直线)
    }

    /**
     * 扇形中心
     */
    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < mCount; i++) {
            PieView2.Coordinate coordinate = mSectorCenters.get(i);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.DKGRAY);
            canvas.drawCircle(coordinate.getX(), coordinate.getY(), 10, paint);
        }
    }


    public void setTextSize(int textSize) {
        mTextSize = textSize;

    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;

    }

    public void setBgColor(String color) {
        mBgColor = Color.parseColor(color);
    }

    public void setBgColor(int bgColor) {
        this.mBgColor = bgColor;
    }

    /**
     * 画背景,白色
     */
    private void drawBg(Canvas canvas) {
        if (canvas == null) return;
        canvas.drawColor(mBgColor);
        if (drawCircle) {
            drawCircle(canvas);
        }
    }

    /**
     * 设置圆边属性
     *
     * @param strokeWidth
     * @param color
     */
    public void setBgCircle(int strokeWidth, int color) {
        if (strokeWidth != 0 && color != 0) drawCircle = true;
        else drawCircle = false;
        this.circleColor = color;
        this.circleStrokeWidth = strokeWidth;
    }

    /**
     * 画圆边
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(circleStrokeWidth);
        canvas.drawCircle(mCenterX, mCenterX, mRadius, paint);
    }

    private class Coordinate {
        Coordinate() {

        }

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int x;
        private int y;
        private int xTag;//圆心左侧-1,圆心右侧1,中心0
        private int yTag;//圆心左侧-1,圆心右侧1,中心0

        int getyTag() {
            return yTag;
        }

        PieView2.Coordinate setyTag(int yTag) {
            this.yTag = yTag;
            return this;
        }

        int getxTag() {
            return xTag;
        }

        PieView2.Coordinate setxTag(int xTag) {
            this.xTag = xTag;
            return this;
        }

        int getX() {
            return x;
        }

        PieView2.Coordinate setX(int x) {
            this.x = x;
            return this;
        }

        int getY() {
            return y;
        }

        PieView2.Coordinate setY(int y) {
            this.y = y;
            return this;
        }
    }


    public interface OnPositionSelectedListener {
        void onPieSelect(int positionSel, int tag);
    }


    public static class PieViewDataBean {
        //      List<Float> ratios, List<Integer> colors, List<String> strings
        private float ratio;
        private int color;
        private String name;

        public float getRatio() {
            return ratio;
        }

        public void setRatio(float ratio) {
            this.ratio = ratio;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private OnPositionSelectedListener onPositionSelectedListener;

    public void setOnSelectedListener(OnPositionSelectedListener listener) {
        this.onPositionSelectedListener = listener;
    }

}
