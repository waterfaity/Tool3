package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;

import com.waterfairy.widget.utils.CanvasUtils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/4/25 09:55
 * @info:
 */
public class AlbumCoverView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "album";
    Runnable runnable;
    Handler handler;//计时
    private boolean isPlaying;//播放中
    boolean hasHandleBackground;//设置背景
    private float centerX, centerY;//旋转中心

    public AlbumCoverView(Context context) {
        this(context, null);
    }


    public AlbumCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void pause() {
        isPlaying = false;
        if (handler != null) {
            handler.removeMessages(0);
        }
    }

    public void start() {
        isPlaying = true;
        if (handler == null) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (getDrawable() != null) {
                        if (centerX == 0 || centerY == 0) {

                            //计算旋转中心
                            float height = getDrawable().getIntrinsicHeight();
                            float width = getDrawable().getIntrinsicWidth();
                            RectF rect = new RectF(0, 0, width, height);
                            getImageMatrix().mapRect(rect);
                            centerX = (rect.right + rect.left) / 2;
                            centerY = (rect.top + rect.bottom) / 2;
                        }
                        //旋转角度  0.5F
                        getImageMatrix().postRotate(0.5F, centerX, centerY);
                        postInvalidate();

                    }
                    if (isPlaying) {
                        handler.postDelayed(runnable, 50);
                    }
                }
            };

        }
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            initBackGround();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void initBackGround() {
        if (hasHandleBackground) return;
        hasHandleBackground = true;
        int width = Math.min(getWidth(), getHeight());//绘制最大宽
        int radiusBig = width / 2;//绘制最大半径
        int padding = radiusBig / 6;//阴影+留白
        int shadowWidth = padding / 3;//阴影
        int circleRadius = radiusBig - shadowWidth;//白色圆
        //padding
        setPadding(padding, padding, padding, padding);

        //创建backGroundBitmap
        Bitmap backgroundBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(backgroundBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //绘制阴影
        CanvasUtils.drawShadow(canvas, radiusBig, shadowWidth, paint);

        //绘制圆
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setShader(null);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(radiusBig, radiusBig, circleRadius, paint);

        //设置backGround
        Drawable drawable = new BitmapDrawable(getResources(), backgroundBitmap);
        setBackgroundDrawable(drawable);
    }
}
