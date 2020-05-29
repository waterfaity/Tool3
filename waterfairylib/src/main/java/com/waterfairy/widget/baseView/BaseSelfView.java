package com.waterfairy.widget.baseView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by water_fairy on 2017/6/13.
 * 995637517@qq.com
 */

public class BaseSelfView extends AppCompatImageView {
    private static final String TAG = "BaseSelfView";
    protected int mWidth, mHeight;
    private ViewDrawObserver viewDrawObserver;
    private int left, right, top, bottom;
    private int currentTimes = 0;
    private int times = 100;//绘画频率
    private int sleepTime = 10;
    private boolean hasDrawFinish;
    protected boolean canDraw;
    private boolean needDraw = true;
    private final int WHAT_DRAW = 0;
    private final int WHAT_FINISH = 1;

    public BaseSelfView(Context context) {
        this(context, null);
    }

    public BaseSelfView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        viewDrawObserver = new ViewDrawObserver();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        left = l;
        right = r;
        top = t;
        bottom = b;
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
                viewDrawObserver.onUpdate(ViewCreateObserver.TYPE_VIEW, true);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canDraw) {
            drawOne(canvas);
        }
    }

    protected void drawOne(Canvas canvas) {

    }

    /**
     * 数据初始化后调用这个
     */
    public void onInitData() {
        viewDrawObserver.onUpdate(ViewCreateObserver.TYPE_DATA, true);
    }

    /**
     * 数据初始化后 调用super.initData();
     */
    protected void onInitDataOk() {
        onInitData();
    }

    /**
     * 数据初始化后 调用super.initData();
     */
    protected void onInitDataOk(boolean needDraw) {
        this.needDraw = needDraw;
        onInitData();
    }

    /**
     * 取消绘制
     */
    public void cancelDraw() {
        if (handler != null) {
            handler.removeMessages(WHAT_DRAW);
            handler.removeMessages(WHAT_FINISH);
            isDrawing = false;
        }
    }

    private class ViewDrawObserver implements ViewCreateObserver {
        private boolean viewState;
        private boolean dataState;

        @Override
        public void onUpdate(int type, boolean state) {
            if (type == TYPE_VIEW) {
                viewState = state;
                if (viewState) {
                    onViewInitOk();
                }
            } else if (type == TYPE_DATA) {
                dataState = state;
            }
            canDraw = false;
            if (viewState && dataState) {
                canDraw = true;
                beforeDraw();
                startDraw();
            }
        }
    }

    protected boolean isDrawing;

    public boolean isDrawing() {
        return isDrawing;
    }

    private Handler handler;//延时时钟

    protected boolean setClock(final OnFloatChangeListener onFloatChangeListener) {
        if (isDrawing) return false;
        isDrawing = true;
        currentTimes = 0;
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == WHAT_DRAW) {
                        if (onFloatChangeListener != null && isDrawing) {
                            float ratio = currentTimes / (float) times;//绘画过的比例
                            startDraw();
                            onFloatChangeListener.onChange(ratio);
                            if (ratio >= 1) {
                                isDrawing = false;
                                removeMessages(WHAT_DRAW);
                                sendEmptyMessageDelayed(WHAT_FINISH, 100);
                            } else if (ratio < 1) {
                                currentTimes++;
                                sendEmptyMessageDelayed(WHAT_DRAW, sleepTime);
                            }
                        }
                    } else if (msg.what == WHAT_FINISH) {
                        onFloatChangeListener.onFinish();
                        isDrawing = false;
                    }
                }
            };
        } else {
            handler.removeMessages(0);
        }
        handler.sendEmptyMessage(0);
        return true;
    }

    /**
     * 界面初始化ok  width,height
     */
    protected void onViewInitOk() {

    }

    private void startDraw() {
        if (needDraw)
            invalidate();
    }

    protected void beforeDraw() {

    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public float getTextLen(String content, float textSize) {
        if (TextUtils.isEmpty(content) || textSize <= 0) return 0;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(content);
    }

    public Rect getTextRect(String content, int textSize) {
        Rect rect = new Rect();
        if (TextUtils.isEmpty(content) || textSize <= 0) return rect;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect;
    }
}
