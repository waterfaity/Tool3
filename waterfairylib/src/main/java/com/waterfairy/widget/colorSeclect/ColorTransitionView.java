package com.waterfairy.widget.colorSeclect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.widget.baseView.BaseSelfView;

/**
 * Created by water_fairy on 2017/7/26.
 * 995637517@qq.com
 */

public class ColorTransitionView extends BaseSelfView implements View.OnTouchListener {
    public final static int STYLE_DEEP = 1;
    public final static int STYLE_ALPHA = 2;
    private Bitmap bitmap;
    private float radio = 1;
    private int startX, endX, totalWidth, circleCenterX;
    private Context context;
    private Paint circlePaint, circlePaintOutLine, linePaintBlue, linePaintGray;
    private int radius;//半径

    private int style = STYLE_DEEP;
    private OnRadioChangeListener onRadioChangeListener;

    public ColorTransitionView(Context context) {
        this(context, null);
    }

    public ColorTransitionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOnTouchListener(this);
        onInitDataOk();
    }

    public void setStyle(int style) {
        this.style = style;
        onInitDataOk();
    }

    public int getStyle() {
        return style;
    }

    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);

        circlePaintOutLine = new Paint();
        circlePaintOutLine.setAntiAlias(true);
        circlePaintOutLine.setColor(Color.parseColor("#cdd1d4"));
        circlePaintOutLine.setStrokeWidth(1);
        circlePaintOutLine.setStyle(Paint.Style.STROKE);

        int strokeWidth = (int) (context.getResources().getDisplayMetrics().density * 2);
        linePaintBlue = new Paint();
        linePaintBlue.setAntiAlias(true);
        linePaintBlue.setColor(Color.parseColor("#40b6ff"));
        linePaintBlue.setStrokeWidth(strokeWidth);

        linePaintGray = new Paint();
        linePaintGray.setAntiAlias(true);
        linePaintGray.setColor(Color.parseColor("#cdd1d4"));
        linePaintGray.setStrokeWidth(strokeWidth);

    }

    @Override
    protected void beforeDraw() {
        initPaint();
        if (bitmap != null) bitmap.recycle();
        startX = mHeight / 2;
        endX = mWidth - mHeight / 2;
        totalWidth = endX - startX;
        circleCenterX = endX;
        radius = (int) (mHeight / 2 - context.getResources().getDisplayMetrics().density * 3);
        if (style == STYLE_DEEP) {
            bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Shader shader = new LinearGradient(0, 0, mWidth, 0, Color.BLACK, Color.WHITE, Shader.TileMode.REPEAT);
            paint.setShader(shader);
            canvas.drawRect(0, 0, mWidth, mHeight, paint);
        } else {
            bitmap = createBg();
            Canvas canvas = new Canvas(bitmap);
            Paint paintShader = new Paint();
            paintShader.setAntiAlias(true);
            Shader shader = new LinearGradient(0, 0, mWidth, 0, Color.TRANSPARENT, Color.WHITE, Shader.TileMode.REPEAT);
            paintShader.setShader(shader);
            canvas.drawRect(0, 0, mWidth, mHeight, paintShader);
        }
    }

    private Bitmap createBg() {
        Bitmap bitmapCell = createBgCell();
        Bitmap repeatHor = createRepeatHor(mWidth, bitmapCell);
        return createRepeatVer(mHeight, repeatHor);
    }

    private Bitmap createBgCell() {
        int width = (int) (context.getResources().getDisplayMetrics().density * 6);
        Bitmap alphaBgBitmap = Bitmap.createBitmap(width * 2, width * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(alphaBgBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, width, paint);
        canvas.drawRect(width, width, 2 * width, 2 * width, paint);
        paint.setColor(Color.GRAY);
        canvas.drawRect(width, 0, 2 * width, width, paint);
        canvas.drawRect(0, width, width, 2 * width, paint);
        return alphaBgBitmap;
    }

    private Bitmap createRepeatHor(int width, Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        count = (width - 1) / src.getWidth() + 1;
        for (int i = 0; i < count; i++) {
            canvas.drawBitmap(src, i * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    private Bitmap createRepeatVer(int height, Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        count = (height - 1) / src.getHeight() + 1;
        for (int i = 0; i < count; i++) {
            canvas.drawBitmap(src, 0, i * src.getHeight(), null);
        }
        return bitmap;
    }

    @Override
    protected void drawOne(Canvas canvas) {
        super.drawOne(canvas);
        drawStatic(canvas);
    }

    /**
     * 绘制当前位置(0->1 左到右)
     *
     * @param canvas
     */
    private void drawStatic(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawLine(startX - radius, mHeight / 2, circleCenterX, mHeight / 2, linePaintBlue);
        canvas.drawLine(circleCenterX, mHeight / 2, mWidth - (startX - radius), mHeight / 2, linePaintGray);
        canvas.drawCircle(circleCenterX, mHeight / 2, radius, circlePaint);
        canvas.drawCircle(circleCenterX, mHeight / 2, radius, circlePaintOutLine);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handlerTouche(event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                handlerTouche(event.getX());
                break;
        }
        return true;
    }

    private void handlerTouche(float eventX) {

        if (eventX >= 0 && eventX <= mWidth) {
            circleCenterX = 0;
            circleCenterX = (int) eventX;
            if (eventX < startX) circleCenterX = startX;
            if (eventX > endX) circleCenterX = endX;
            radio = (circleCenterX - startX) / (float) totalWidth;
            if (onRadioChangeListener != null) onRadioChangeListener.onRadioChange(this, radio);
            postInvalidate();
        }
    }

    public void setOnRadioChangeListener(OnRadioChangeListener onRadioChangeListener) {
        this.onRadioChangeListener = onRadioChangeListener;

    }

    public interface OnRadioChangeListener {
        void onRadioChange(ColorTransitionView colorTransitionView, float radio);
    }
}
