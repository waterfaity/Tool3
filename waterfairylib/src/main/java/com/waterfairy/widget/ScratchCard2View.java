package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/30 11:11
 * @info:
 */
public class ScratchCard2View extends BaseView {

    private static final float TOUCH_TOLERANCE = 4;

    private int mColorScratch;
    private int mColorScratchStroke;
    private Paint mPaint;
    private Paint eraserPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private float mX;
    private float mY;
    private boolean shutDown;
    private String text;
    private float textSize;
    private int imgRes;

    public ScratchCard2View(Context context) {
        this(context, null);
    }

    public ScratchCard2View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mColorScratch = Color.parseColor("#6da6ee");
        mColorScratchStroke = Color.parseColor("#cee4ff");
        initPaint();

    }

    public void setText(String text, float textSize) {
        this.text = text;
        this.textSize = textSize;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColorScratch);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(getResources().getDisplayMetrics().density * 8);


        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(true);
        eraserPaint.setAlpha(0);
        eraserPaint.setStrokeWidth(70);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setDither(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }


    @Override
    public void onViewMeasure(boolean changed, int width, int height) {
        super.onViewMeasure(changed, width, height);

        //前景
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgRes);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawBitmap(bitmap,
                new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, getWidth(), getHeight()), null);


        //设置背景
        Bitmap bgBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas bgCanvas = new Canvas(bgBitmap);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorScratch);
        bgCanvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColorScratchStroke);
        bgCanvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        if (!TextUtils.isEmpty(text)) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            paint.setColor(Color.WHITE);
            Rect textRect = getTextRect(text, (int) textSize);
            bgCanvas.drawText(text, (getWidth() - textRect.width()) / 2, (getHeight() - textRect.height()) / 2 + textRect.height(), paint);
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bgBitmap);
        setBackground(bitmapDrawable);
        mPath = new Path();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                shutDown = true;
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                shutDown = false;
                touch_up();
                invalidate();
                calcArea();
                break;
        }
        return true;

    }

    private void calcArea() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (onScratchListener != null) {
                    if (mBitmap != null && !mBitmap.isRecycled()) {
                        int divide = 5;
                        int totalPix = (int) (mBitmap.getWidth() * mBitmap.getHeight() / Math.pow(divide, 2));
                        int tempPix = 0;
                        for (int i = 0; i < mBitmap.getWidth(); i += divide) {
                            for (int j = 0; j < mBitmap.getHeight(); j += divide) {
                                if (!shutDown) {
                                    if (0 == Color.alpha(mBitmap.getPixel(i, j))) {
                                        tempPix++;
                                    }
                                } else {
                                    return null;
                                }
                            }
                        }
                        onScratchListener.onScratch(ScratchCard2View.this, tempPix / (float) totalPix);
                    }
                }
                return null;
            }
        }.execute();

    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, eraserPaint);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            mCanvas.drawPath(mPath, eraserPaint);
        }

    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        mCanvas.drawPath(mPath, eraserPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    private OnScratchListener onScratchListener;

    public void setOnScratchListener(OnScratchListener onScratchListener) {
        this.onScratchListener = onScratchListener;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public interface OnScratchListener {
        void onScratch(ScratchCard2View scratchCardView, float radio);
    }

}
