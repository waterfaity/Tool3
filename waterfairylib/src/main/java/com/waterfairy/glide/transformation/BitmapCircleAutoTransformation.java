package com.waterfairy.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.waterfairy.utils.ImageUtils;

import java.util.Arrays;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/10/16 09:36
 * @info:
 */
public class BitmapCircleAutoTransformation extends BitmapTransformation {
    private int radius;
    private String key = "circle_img";
    private int corners[];


    public BitmapCircleAutoTransformation(Context context) {
        super(context);
    }

    public BitmapCircleAutoTransformation(Context context, int radius) {
        super(context);
        this.radius = radius;

    }

    public BitmapCircleAutoTransformation(Context context, int radius, int... corners) {
        super(context);
        this.radius = radius;
        this.corners = corners;

    }

    public BitmapCircleAutoTransformation(Context context, int radius, String key, int... corners) {
        super(context);
        this.radius = radius;
        this.corners = corners;
        this.key = key;

    }

    public BitmapCircleAutoTransformation(Context context, int radius, String key) {
        super(context);
        this.radius = radius;
        this.key = key;
    }

    @Override
    public Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
        if (radius == 0) {
            return circleBitmap(bitmapPool, bitmap);
        } else {
            return circleAngleBitmap(bitmapPool, bitmap);
        }
    }

    private Bitmap circleAngleBitmap(BitmapPool bitmapPool, Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (size < radius) {
            radius = size;
        }
        return ImageUtils.round(source, radius,   corners == null ? new int[]{0, 1, 2, 3} : corners  );
    }

    private Bitmap circleBitmap(BitmapPool bitmapPool, Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int size = Math.min(width, height);
        Bitmap outBitmap = bitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
        if (outBitmap == null) outBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        Bitmap bitmapTemp = Bitmap.createBitmap(source, (width - size) / 2, (height - size) / 2, size, size);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmapTemp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        int radius = size / 2;
        canvas.drawCircle(radius, radius, radius, paint);
        if (!source.isRecycled()) {
            source.recycle();
            source = null;
        }
        return outBitmap;
    }

    @Override
    public String getId() {
        return key + (corners == null ? Arrays.toString(new int[]{0, 1, 2, 3}) : Arrays.toString(corners));
    }
}
