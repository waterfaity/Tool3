package com.waterfairy.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;

/**
 * Created by water_fairy on 2017/7/26.
 * 995637517@qq.com
 */

public class RGBColorBitmapCreator {

    private int mWidth, mHeight;
    private Paint sweepPaint, radialPaint;

    public RGBColorBitmapCreator(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public Bitmap createBitmap() {

        setPaint();
        return drawBitmap();
    }

    private Bitmap drawBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mHeight / 2, sweepPaint);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mHeight / 2, radialPaint);
        return bitmap;
    }

    private void setPaint() {
        SweepGradient sweepGradient = new SweepGradient(mWidth / 2, mHeight / 2,
                new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED}, null);
        sweepPaint = new Paint();
        sweepPaint.setAntiAlias(true);
        sweepPaint.setShader(sweepGradient);

        RadialGradient radialGradient = new RadialGradient(mWidth / 2, mHeight / 2, Math.max(mHeight, mWidth) / 2, new int[]{Color.WHITE, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        radialPaint = new Paint();
        radialPaint.setAntiAlias(true);
        radialPaint.setShader(radialGradient);

    }
}
