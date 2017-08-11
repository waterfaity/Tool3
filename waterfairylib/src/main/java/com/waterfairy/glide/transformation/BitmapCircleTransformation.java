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

/**
 * 图片圆角
 * Created by water_fairy on 2017/6/6.
 * 995637517@qq.com
 */

public class BitmapCircleTransformation extends BitmapTransformation {
    private int radius;
    private String key = "circle_img";


    public BitmapCircleTransformation(Context context) {
        super(context);
    }

    public BitmapCircleTransformation(Context context, int radius) {
        super(context);
        this.radius = radius;

    }

    public BitmapCircleTransformation(Context context, int radius, String key) {
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
        return ImageUtils.round(source, radius);
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
        return outBitmap;
    }

    @Override
    public String getId() {
        return key;
    }

}
