package com.waterfairy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.waterfairy.widget.utils.CanvasUtils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/31 17:31
 * @info: 生成icon
 */
public class IconUtils {
    /**
     * @param radius 圆角半径 气泡
     * @return
     */
    public static Bitmap createBubble(int radius, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        Bitmap bitmap = Bitmap.createBitmap(radius * 5, ((int) (radius * 3.75F)), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        CanvasUtils.drawCorner(canvas, new RectF(0, 0, bitmap.getWidth(), (int) (2.6F * radius)), radius, 1, color, color, null);
        Path path = new Path();
        path.moveTo(1.5F * radius, 2.5F * radius);
        path.lineTo(3.5F * radius, 2.5F * radius);
        path.lineTo(2.5F * radius, 3.75F * radius);
        path.lineTo(1.5F * radius, 2.5F * radius);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    public static final int DENSITY_XXXH = 640;
    public static final int DENSITY_XXH = 480;
    public static final int DENSITY_XH = 320;
    public static final int DENSITY_H = 240;
    public static final int DENSITY_M = 160;
    public static final int DENSITY_L = 120;

    public static Bitmap decodeBitmap(Context context, int resId, int inDensity, int inTargetDensity) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = inDensity;
        options.inTargetDensity = inTargetDensity;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
}
