package com.waterfairy.widget.colorSeclect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.utils.AlphaBgImgCreator;
import com.waterfairy.utils.AssetsUtils;
import com.waterfairy.widget.baseView.BaseSelfView;

import java.io.IOException;

/**
 * Created by water_fairy on 2017/7/26.
 * 995637517@qq.com
 */

public class ColorCircleView extends BaseSelfView implements View.OnTouchListener {
    private final String TAG = "ColorCircleView";
    private Paint sweepPaint, radialPaint;
    private Bitmap bitmapAlpha, colorBitmap;
    private Paint paintAlpha, paintDeep, bgAlphaPaint;
    private int cellWidth, radius;
    private int colorWidth, colorHeight;
    private OnColorSelectListener onColoSelectListener;
    private int deep;
    private int alpha = 0;
    private int touchX, touchY;
    private int lastColor;

    public ColorCircleView(Context context) {
        this(context, null);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        cellWidth = (int) (context.getResources().getDisplayMetrics().density * 6);
        try {
            colorBitmap = BitmapFactory.decodeStream(AssetsUtils.getIS(context, "img/circle_1.png"));
            colorWidth = colorBitmap.getWidth();
            colorHeight = colorBitmap.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onInitDataOk();
    }

    @Override
    protected void beforeDraw() {
        radius = mWidth / 2;
        SweepGradient sweepGradient = new SweepGradient(mWidth / 2, mHeight / 2,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED}, null);
        sweepPaint = new Paint();
        sweepPaint.setAntiAlias(true);
        sweepPaint.setShader(sweepGradient);

        RadialGradient radialGradient = new RadialGradient(mWidth / 2, mHeight / 2, mHeight / 2, new int[]{Color.WHITE, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        radialPaint = new Paint();
        radialPaint.setAntiAlias(true);
        radialPaint.setShader(radialGradient);

        bitmapAlpha = new AlphaBgImgCreator(mWidth, mHeight, cellWidth).createCircleBg();

        paintAlpha = new Paint();
        paintAlpha.setAntiAlias(true);
        paintAlpha.setAlpha(0);
        paintDeep = new Paint();
        paintDeep.setAntiAlias(true);
        paintDeep.setColor(Color.TRANSPARENT);


        bgAlphaPaint = new Paint();
        bgAlphaPaint.setAntiAlias(true);
        bgAlphaPaint.setColor(Color.TRANSPARENT);
//        bitmapColor = new RGBColorBitmapCreator(mWidth, mHeight).createBitmap();

    }

    @Override
    protected void drawOne(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mHeight / 2, mHeight / 2, sweepPaint);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mHeight / 2, radialPaint);

        canvas.drawCircle(mWidth / 2, mHeight / 2, mHeight / 2, paintAlpha);
        canvas.drawBitmap(bitmapAlpha, 0, 0, bgAlphaPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleTouchPoint(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchPoint(event.getX(), event.getY());
                break;
        }
        return true;
    }

    private void handleTouchPoint(float x, float y) {
        touchX = (int) x;
        touchY = (int) y;
        if (Math.pow(x - radius, 2) + Math.pow(y - radius, 2) < Math.pow(radius, 2)) {
            int colorX = (int) (x / mWidth * colorWidth);
            int colorY = (int) (y / mHeight * colorHeight);
            lastColor = getColor(colorX, colorY);
            handleColor(lastColor);
        }
    }

    private void handleColor(int color) {
        float radio = 1 - deep / 255f;
        int red = (int) (Color.red(color) * radio);
        int green = (int) (Color.green(color) * radio);
        int blue = (int) (Color.blue(color) * radio);
        color = Color.argb(255 - alpha, red, green, blue);
        if (onColoSelectListener != null) onColoSelectListener.onColorSelect(color);
    }


    public void updateAlpha(float alphaRadio) {
        alpha = 255 - (int) (255 * alphaRadio);
        bgAlphaPaint.setAlpha(alpha);
        postInvalidate();
        handleColor(lastColor);
    }

    public void updateDeep(float deepRadio) {
        deep = 255 - (int) (255 * deepRadio);
        String s = Integer.toHexString(deep);
        if (s.length() == 1) s = "0" + s;
        Log.i(TAG, "updateDeep: " + s);
        paintAlpha.setColor(Color.parseColor("#" + s + "000000"));
        postInvalidate();
        handleColor(lastColor);
    }

    private String getHex(int num) {
        String s = Integer.toHexString(deep);
        if (s.length() == 1) s = "0" + s;
        return s;
    }

    public int getColor(int x, int y) {
        if (colorBitmap == null) return Color.WHITE;
        else {
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            int bitmapWidth = colorBitmap.getWidth();
            int bitmapHeight = colorBitmap.getHeight();
            return colorBitmap.getPixel(x >= bitmapWidth ? bitmapWidth - 1 : x, y >= bitmapHeight ? bitmapHeight - 1 : y);
        }
    }

    public void setOnColoSelectListener(OnColorSelectListener onColoSelectListener) {
        this.onColoSelectListener = onColoSelectListener;
    }
}
