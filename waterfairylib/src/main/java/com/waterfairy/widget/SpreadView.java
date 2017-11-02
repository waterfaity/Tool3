package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/1
 * @Description:
 */

public class SpreadView extends View {

    private int mWidth, mHeight;//view 宽高
    private int mColor = Color.BLUE;//颜色
    private int mCurrentTimes;//当前次数
    private int mTimes = 100;//整个过程刷新次数
    private int mSleepTime = 3;//每次刷新休眠时间
    private boolean canDraw;//是否可以绘制
    private int mMaxRadius;//最大半径
    private int mCurrentRadius;//当前半径
    private Paint mPaint;//画笔
    private int mCenterX, mCenterY;
    private boolean mSpread;//扩展 /收缩
    private OnSpreadListener spreadListener;//绘制完成监听

    public SpreadView(Context context) {
        super(context);
    }


    public SpreadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);//抗锯齿

    }

    /**
     * 获取 view 宽 高
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        int height = b - t;
        if (width != 0 && height != 0) {
            boolean change = false;
            if (width != this.mWidth) {
                this.mWidth = width;
                change = true;
            }
            if (height != this.mHeight) {
                this.mHeight = height;
                change = true;
            }
            if (change) {
                //界面初始化完成
                viewDrawObserver.onUpdate(ViewCreateObserver.TYPE_VIEW, true);
            }
        }
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canDraw) {
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawCircle(mCenterX, mCenterY, mCurrentRadius, mPaint);
        }
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    /**
     * 刷新次数
     *
     * @param times
     */
    public void setTimes(int times) {
        this.mTimes = times;
    }

    /**
     * 每次休眠时间
     *
     * @param sleepTime
     */
    public void setSleepTime(int sleepTime) {
        this.mSleepTime = sleepTime;
    }

    /**
     * 启动
     *
     * @param spread 扩展 true / 收缩 false
     * @param x      起始x
     * @param y      起始
     */
    public void start(boolean spread, int x, int y) {
        mSpread = spread;
        mCenterX = x;
        mCenterY = y;
        onDataInitOk();
    }

    /**
     * 计算半径
     */
    private void calcRadius() {
        //判断区域 四块(左上,左下 ...)
        boolean isLeft = false;
        if (mWidth / 2 > mCenterX) {
            isLeft = true;
        }
        boolean isTop = false;
        if (mHeight / 2 > mCenterY) {
            isTop = true;
        }
        if (isLeft) {
            if (isTop) {
                //左上区域 距离右下最远
                mMaxRadius = (int) Math.sqrt(Math.pow(mCenterX - mWidth, 2) + Math.pow(mCenterY - mHeight, 2));
            } else {
                //左下区域 距离右上最远
                mMaxRadius = (int) Math.sqrt(Math.pow(mCenterX - mWidth, 2) + Math.pow(mCenterY, 2));
            }
        } else {
            if (isTop) {
                //右上区域 距离左下最远
                mMaxRadius = (int) Math.sqrt(Math.pow(mCenterX, 2) + Math.pow(mCenterY - mHeight, 2));
            } else {
                //右下区域 距离左上最远
                mMaxRadius = (int) Math.sqrt(mCenterX * mCenterX + mCenterY * mCenterY);
            }
        }
    }

    /**
     * 数据初始化完成
     */
    public void onDataInitOk() {
        viewDrawObserver.onUpdate(ViewCreateObserver.TYPE_DATA, true);
    }

    private void initBeforeDraw() {
        calcRadius();
        mCurrentTimes = 0;
        timeClock.removeMessages(0);
        timeClock.sendEmptyMessage(0);

    }

    /**
     * 延时绘画 时钟
     */
    Handler timeClock = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mCurrentTimes <= mTimes) {
                float radio = mCurrentTimes / (float) mTimes;
                mCurrentRadius = (int) (mMaxRadius * (mSpread ? radio : (1 - radio)));
                invalidate();
                mCurrentTimes++;
                timeClock.sendEmptyMessageDelayed(0, mSleepTime);
            } else {
                if (spreadListener != null) {
                    spreadListener.onSpreadOk();
                }
            }
        }
    };

    /**
     * 观察
     * 1.view是否初始化完成
     * 2.数据是否初始化完成
     * 两者都true  可以绘制 invalidate -> onDraw();
     */
    private ViewCreateObserver viewDrawObserver = new ViewCreateObserver() {
        private boolean viewState;
        private boolean dataState;

        @Override
        public void onUpdate(int type, boolean state) {
            if (type == TYPE_VIEW) {
                viewState = state;
            } else if (type == TYPE_DATA) {
                dataState = state;
            }
            if (viewState && dataState) {
                canDraw = true;
                initBeforeDraw();
            }
        }
    };


    public interface ViewCreateObserver {
        int TYPE_DATA = 1;
        int TYPE_VIEW = 2;

        void onUpdate(int type, boolean state);
    }


    public void setOnSpreadListener(OnSpreadListener spreadListener) {
        this.spreadListener = spreadListener;
    }

    public interface OnSpreadListener {
        void onSpreadOk();
    }
}
