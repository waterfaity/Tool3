package com.waterfairy.widget.flipView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import androidx.collection.LruCache;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseSurfaceView;
import com.waterfairy.widget.baseView.OnCanvasChangeListener;

/**
 * user : water_fairy
 * email:995637517@qq.com
 * date :2017/10/22
 * des  : surfaceView 双缓冲绘图
 */

public class FlipView2 extends BaseSurfaceView implements OnCanvasChangeListener {
    private static final String TAG = "pageView";
    private FlipAdapter adapter;//adapter
    private int viewWidth, viewHeight, leftEnd, rightStart;//宽,高,左边一半右侧,右边一半左侧
    private LruCache<Integer, Bitmap> lruCache;//缓存图片bitmap
    private int currentPos;//当前位置
    private int totalNum;
    private Rect leftViewRect, rightViewRect;//画板左侧,画板右侧

    private float startX = 0;//开始x坐标
    private float radio;//旋转指数 右滑: 0->1 , 左滑: 0->-1
    private OnFlipListener flipListener;//滑动监听
    private OnClickListener onClickListener;//点击事件
    private final int MIN_SCROLL_LEN = 10;
    private boolean canTurn = true;
    private Bitmap cacheBitmap;
    private Canvas cacheCanvas;


    public FlipView2(Context context) {
        this(context, null);
    }

    public FlipView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setZOrderMediaOverlay(true);
        setZOrderOnTop(false);
        initLruCache();
    }

    @Override
    protected void onViewInitOk() {
        super.onViewInitOk();
        cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        cacheCanvas = new Canvas(cacheBitmap);
    }

    /**
     * 缓存处理
     */
    private void initLruCache() {
//        lruCache = new LruCache<>(3);
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 16);
        lruCache = new LruCache<Integer, Bitmap>(cacheSize) {
            //            必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    protected void beforeDraw() {
        radio = 0;
        viewHeight = mHeight;
        viewWidth = mWidth;
        leftEnd = viewWidth / 2;
        rightStart = viewWidth - leftEnd;

        if (lruCache == null) {
            initLruCache();
        } else {
            lruCache.evictAll();
        }
        currentPos = 0;
        cacheBitmap(0);
        calcViewSide(cacheBitmap(1));

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        if (flipListener != null) {
            flipListener.onFlipPageSelect(currentPos);
        }
    }

    @Override
    protected void startDraw() {
        setClockNo();
    }

    @Override
    protected void drawOne(Canvas canvas) {
        super.drawOne(canvas);
        try {
            onDrawChange(canvas, 1f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawChange(Canvas cacheCanvas, float value) throws Exception {
//        cacheCanvas
        long startTime = System.currentTimeMillis();
//        cacheCanvas.drawColor(Color.WHITE);
        radio = sign * (float) ((Math.sin((value - 0.5f) * Math.PI) + 1) / 2f);
        if (viewHeight != 0 && adapter != null) {
            if (radio > 0) {
                //右滑 -> 当前右侧不动 pre左侧改变
                Bitmap currentBitmap = getBitmap(currentPos);
                Bitmap preBitmap = getBitmap(currentPos - 1);
                int viewCenterX = (int) ((rightViewRect.right - leftViewRect.left) * radio);
                if (Math.abs(radio) < 0.5f) {
                    int bitmapCenterX = (int) (preBitmap.getWidth() * radio);
                    long l1 = System.currentTimeMillis();
                    //左侧:(1.srcBitmap 2.bitmap 区域  3.view的区域)
                    cacheCanvas.drawBitmap(preBitmap, getBitmapLeftRect(preBitmap, bitmapCenterX),
                            new RectF(leftViewRect.left, leftViewRect.top, leftViewRect.left + viewCenterX, leftViewRect.bottom), null);//左侧
                    long l2 = System.currentTimeMillis();
                    //中间:
                    RectF centerRect = new RectF(leftViewRect.left + viewCenterX, leftViewRect.top, leftViewRect.right, leftViewRect.bottom);
                    cacheCanvas.drawBitmap(currentBitmap, getBitmapLeftRect(currentBitmap,
                            currentBitmap.getWidth() / 2),
                            centerRect, null);//中
                    long l3 = System.currentTimeMillis();
//                    画阴影
                    cacheCanvas.drawRect(centerRect, getPaint(radio, centerRect.left, centerRect.right));
                    long l4 = System.currentTimeMillis();
                    //右侧:
                    cacheCanvas.drawBitmap(currentBitmap,
                            getBitmapRightRect(currentBitmap, currentBitmap.getWidth() / 2),
                            new RectF(rightViewRect.left, rightViewRect.top, rightViewRect.right, rightViewRect.bottom), null);
                    long l5 = System.currentTimeMillis();

                    Log.i(TAG, "onDrawChange: 左" + (l2 - l1));
                    Log.i(TAG, "onDrawChange: 中" + (l3 - l2));
                    Log.i(TAG, "onDrawChange: 影" + (l4 - l3));
                    Log.i(TAG, "onDrawChange: 右" + (l5 - l4));

                } else {
                    int bitmapCenterX = (int) (currentBitmap.getWidth() * radio);
                    //左侧:
                    cacheCanvas.drawBitmap(preBitmap,
                            getBitmapLeftRect(preBitmap, preBitmap.getWidth() / 2),
                            new RectF(leftViewRect.left, leftViewRect.top, leftViewRect.right, leftViewRect.bottom), null);//左侧
                    //中间:
                    RectF centerRectF = new RectF(rightViewRect.left, leftViewRect.top, leftViewRect.left + viewCenterX, rightViewRect.bottom);
                    cacheCanvas.drawBitmap(preBitmap, getBitmapRightRect(preBitmap, preBitmap.getWidth() / 2),
                            centerRectF, null);//中
//                    画阴影
                    cacheCanvas.drawRect(centerRectF, getPaint(radio, centerRectF.left, centerRectF.right));

                    //右侧:
                    cacheCanvas.drawBitmap(currentBitmap,
                            getBitmapRightRect(currentBitmap, bitmapCenterX),
                            new RectF(leftViewRect.left + viewCenterX, rightViewRect.top, rightViewRect.right, rightViewRect.bottom), null);
                }
            } else if (radio < 0) {

                float radioTemp = -radio;
                //左滑 <- 左侧不动 右侧改变
                Bitmap currentBitmap = getBitmap(currentPos);
                Bitmap nextBitmap = getBitmap(currentPos + 1);
                int viewCenterX = (int) ((rightViewRect.right - leftViewRect.left) * (1 - radioTemp));

                if (Math.abs(radioTemp) < 0.5f) {
                    int bitmapCenterX = (int) (nextBitmap.getWidth() * (1 - radioTemp));
                    //左侧:
                    cacheCanvas.drawBitmap(currentBitmap,
                            getBitmapLeftRect(currentBitmap, currentBitmap.getWidth() / 2),
                            new RectF(leftViewRect.left, leftViewRect.top, leftViewRect.right, leftViewRect.bottom), null);//左侧
                    //中间:
                    RectF centerRectF = new RectF(rightViewRect.left, rightViewRect.top, leftViewRect.left + viewCenterX, rightViewRect.bottom);
                    cacheCanvas.drawBitmap(currentBitmap,
                            getBitmapRightRect(currentBitmap, currentBitmap.getWidth() - currentBitmap.getWidth() / 2),
                            centerRectF, null);//中
//                    画阴影
                    cacheCanvas.drawRect(centerRectF, getPaint(radio, centerRectF.left, centerRectF.right));
                    //右侧:
                    cacheCanvas.drawBitmap(nextBitmap,
                            getBitmapRightRect(nextBitmap, bitmapCenterX),
                            new RectF(leftViewRect.left + viewCenterX, rightViewRect.top, rightViewRect.right, rightViewRect.bottom), null);
                } else {
                    int bitmapCenterX = (int) (currentBitmap.getWidth() * (1 - radioTemp));
                    //左侧:
                    cacheCanvas.drawBitmap(currentBitmap,
                            getBitmapLeftRect(currentBitmap, bitmapCenterX),
                            new RectF(leftViewRect.left, leftViewRect.top, leftViewRect.left + viewCenterX, leftViewRect.bottom), null);//左侧
                    //中间:
                    RectF centerRectF = new RectF(leftViewRect.left + viewCenterX, leftViewRect.top, leftViewRect.right, leftViewRect.bottom);
                    cacheCanvas.drawBitmap(nextBitmap,
                            getBitmapLeftRect(nextBitmap, nextBitmap.getWidth() - nextBitmap.getWidth() / 2),
                            centerRectF, null);//中
//                    画阴影
                    cacheCanvas.drawRect(centerRectF, getPaint(radio, centerRectF.left, centerRectF.right));
                    //右侧:
                    cacheCanvas.drawBitmap(nextBitmap,
                            getBitmapRightRect(nextBitmap, nextBitmap.getWidth() - nextBitmap.getWidth() / 2),
                            new RectF(rightViewRect.left, rightViewRect.top, rightViewRect.right, rightViewRect.bottom), null);
                }
            } else if (radio == 0) {
                Bitmap bitmap = getBitmap(currentPos);
                cacheCanvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        new Rect(leftViewRect.left, leftViewRect.top, rightViewRect.right, rightViewRect.bottom), null);//静止
            }
        }
        if (value == 0) {
            if (flipListener != null) flipListener.onFlipping(true);
        } else if (value == 1) {
            calcCurrentPos();
            cacheBitmapSide();
            if (flipListener != null) flipListener.onFlipping(false);
            radio = 0;
            sign = 0;
        }
        long l8 = System.currentTimeMillis();
//        canvas.drawBitmap(cacheBitmap, 0, 0, null);
        Log.i(TAG, "onDrawChange: 绘制用时: " + (System.currentTimeMillis() - startTime));
//        Log.i(TAG, "onDrawChange: 绘制用时重绘: " + (System.currentTimeMillis() - l8));
//        Log.i(TAG, "onDrawChange: 缓存绘制: " + (l8 - startTime));
    }

    @Override
    protected void drawFinishView(Canvas canvas) {
        try {
            onDrawChange(canvas, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取bitmap
     *
     * @param pos
     * @return
     */
    private Bitmap getBitmap(int pos) {
        if (pos < 0 || pos > totalNum - 1)
            return Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        if (lruCache == null) return null;
        Bitmap bitmap = lruCache.get(pos);
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = adapter.getBitmap(pos);
            try {
                lruCache.put(pos, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (adapter != null && !isDrawing) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    calcUpX(event.getX() - startX);
                    break;
            }
            return true;
        } else return false;
    }

    private int sign;

    /**
     * 处理抬手事件
     */
    private void turnPage(boolean right) {
        if (!canTurn) return;
        sign = right ? 1 : -1;
        setClock(this, 30);
    }

    private void calcCurrentPos() {
        if (sign == 1) currentPos--;
        else if (sign == -1) currentPos++;
        if (flipListener != null && sign != 0) {
            flipListener.onFlipPageSelect(currentPos);
            Log.i(TAG, "calcCurrentPos: " + currentPos);
        }
    }


    /**
     * 缓存当前位置左右俩个页面
     */
    private void cacheBitmapSide() {
        if (currentPos > 0) {
            cacheBitmap(currentPos - 1);
        }
        if (currentPos < totalNum - 1) {
            cacheBitmap(currentPos + 1);
        }
    }


    /**
     * 计算滑动距离
     *
     * @param dela
     */
    private void calcUpX(float dela) {
        if (adapter == null) return;
        if (Math.abs(dela) < MIN_SCROLL_LEN || !canTurn) {
            if (onClickListener != null) onClickListener.onClick(this);
            return;
        }
        if (viewHeight != 0 && ((dela > 0 && currentPos > 0) || (dela < 0 && currentPos < totalNum - 1))) {
            radio = dela / (viewHeight / 3);
            radio = radio > 1 ? 1 : radio < -1 ? -1 : radio;
            if (radio > 0.5f) {
                //右滑
                turnPage(true);
            } else if (radio < -0.5f) {
                //左滑
                turnPage(false);
            }
        } else {
            if (flipListener != null) {
                if (dela >= 0 && currentPos <= 0) {
                    flipListener.onFlipLeftEdge();
                } else if (dela <= 0 && currentPos >= totalNum - 1) {
                    flipListener.onFlipRightEdge();
                }
            }
            radio = 0;
        }
    }

    private Paint shadowPaint = new Paint();

    /**
     * 阴影paint
     *
     * @param radio
     * @param left
     * @param right
     * @return
     */
    private Paint getPaint(float radio, float left, float right) {
        float radioTemp = Math.abs(radio);
        if (radioTemp < 0.5) {
            if (radio > 0) {
                //左侧深
                shadowPaint.setShader(new LinearGradient(left, 0, right, 0, argb(radioTemp, 0, 0, 0), Color.TRANSPARENT, Shader.TileMode.MIRROR));
            } else {
                //右侧深
                shadowPaint.setShader(new LinearGradient(left, 0, right, 0, Color.TRANSPARENT, argb(radioTemp, 0, 0, 0), Shader.TileMode.MIRROR));
            }
        } else {
            if (radio > 0) {
                //右侧深
                shadowPaint.setShader(new LinearGradient(left, 0, right, 0, Color.TRANSPARENT, argb(1 - radioTemp, 0, 0, 0), Shader.TileMode.MIRROR));
            } else {
                //左侧深
                shadowPaint.setShader(new LinearGradient(left, 0, right, 0, argb(1 - radioTemp, 0, 0, 0), Color.TRANSPARENT, Shader.TileMode.MIRROR));
            }
        }
        return shadowPaint;
    }

    public static int argb(float alpha, float red, float green, float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) << 8) |
                (int) (blue * 255.0f + 0.5f);
    }

    public void setAdapter(FlipAdapter adapter) {
        this.adapter = adapter;
        totalNum = adapter.getCount();
        onInitDataOk();
    }

    /**
     * 缓存指定位置的bitmap
     *
     * @param pos
     * @return
     */
    private Bitmap cacheBitmap(int pos) {
        Bitmap cacheBitmap = lruCache.get(pos);
        if (cacheBitmap == null || cacheBitmap.isRecycled()) {
            cacheBitmap = adapter.getBitmap(pos);
            lruCache.put(pos, cacheBitmap);
//            Log.i(TAG, "cacheBitmap: -缓存大小:" + lruCache.size() / (1024 * 1024) + "M  -当前:" + pos + "  -宽:" + cacheBitmap.getWidth() + "  -高:" + cacheBitmap.getHeight());
        }
        return cacheBitmap;
    }

    /**
     * 计算绘制到view的边缘
     *
     * @param cacheBitmap
     */
    private void calcViewSide(Bitmap cacheBitmap) {

        leftViewRect = new Rect();
        rightViewRect = new Rect();
        if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
            int bitmapWidth = cacheBitmap.getWidth();
            int bitmapHeight = cacheBitmap.getHeight();

            if (bitmapWidth / (float) bitmapHeight > viewWidth / (float) viewHeight) {
                //图片宽度 max
                float viewHeightTemp = bitmapHeight / (float) bitmapWidth * viewWidth;
                int top = (int) ((viewHeight - viewHeightTemp) / 2);
                int bottom = viewHeight - top;
                leftViewRect = new Rect(0, top, leftEnd, bottom);
                rightViewRect = new Rect(rightStart, top, viewWidth, bottom);
            } else {
                //图片高度 max
                float viewWidthTemp = bitmapWidth / (float) bitmapHeight * viewHeight;
                int left = (int) ((viewWidth - viewWidthTemp) / 2);
                int right = viewWidth - left;
                leftViewRect = new Rect(left, 0, leftEnd, viewHeight);
                rightViewRect = new Rect(rightStart, 0, right, viewHeight);
            }
        }
    }

    /**
     * 获取bitmap 左半边
     *
     * @param bitmap
     * @param rightX
     * @return
     */
    private Rect getBitmapLeftRect(Bitmap bitmap, int rightX) {
        if (bitmap == null || bitmap.isRecycled()) {
            return new Rect(0, 0, 0, 0);
        } else {
            return new Rect(0, 0, rightX, bitmap.getHeight());
        }

    }

    /**
     * 获取bitmap 右半边
     *
     * @param bitmap
     * @param leftX
     * @return
     */
    private Rect getBitmapRightRect(Bitmap bitmap, int leftX) {
        if (bitmap == null || bitmap.isRecycled()) {
            return new Rect(0, 0, 0, 0);
        } else {
            int width = bitmap.getWidth();
            return new Rect(leftX, 0, width, bitmap.getHeight());
        }
    }

    public void setOnFlipListener(OnFlipListener flipListener) {
        this.flipListener = flipListener;
    }

    public void turnPre() {
        if (isDrawing || currentPos == 0) return;
        turnPage(true);
    }

    public void turnNext() {
        if (isDrawing || currentPos > totalNum - 1) return;
        turnPage(false);
    }

    public void setCurrentPage(int currentPos) {
        if (!canTurn) return;
        this.currentPos = currentPos;
        super.cancelDraw();
        radio = 0;
        if (adapter == null || totalNum == 0 || totalNum - 1 < currentPos) {
            this.currentPos = 0;
        } else {
            cacheBitmap(currentPos);
            invalidate();
        }
        if (flipListener != null)
            flipListener.onFlipPageSelect(currentPos);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FlipAdapter getAdapter() {
        return adapter;
    }

    public void setCanTurn(boolean canTurn) {
        this.canTurn = canTurn;
    }


}
