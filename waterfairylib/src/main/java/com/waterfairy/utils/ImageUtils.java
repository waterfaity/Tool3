package com.waterfairy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by water_fairy on 2017/2/21.
 */

public class ImageUtils {
    private static final String TAG = "imgUtils";

    public static boolean saveBitmap(String imgPath, Bitmap source) {
        return saveBitmap(imgPath, source, Bitmap.CompressFormat.JPEG, 90);
    }

    public static boolean saveBitmap(String imgPath, Bitmap source, Bitmap.CompressFormat compressFormat, int quality) {
        Log.i(TAG, "saveBitmap: " + imgPath);
        File file = new File(imgPath);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            source.compress(compressFormat, quality, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap compress(Bitmap source, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        source.compress(compressFormat, quality, baos);
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return BitmapFactory.decodeStream(bais);
    }

    /**
     * 尺寸缩放
     *
     * @param source 源bitmap
     * @param width  目标宽px
     * @param height 目标高px
     * @param fitXY  等比缩放?
     * @return
     */
    public static Bitmap matrix(Bitmap source, int width, int height, boolean fitXY) {
        if (source != null) {
            int bitmapWidth = source.getWidth();
            int bitmapHeight = source.getHeight();
            float xScale = 1;
            float yScale = 1;
            if (fitXY) {
                if (bitmapWidth > width) {
                    xScale = width / (float) bitmapWidth;
                }
                if (bitmapHeight > height) {
                    yScale = height / (float) bitmapHeight;
                }
            } else {
                boolean isWidthBig = false;
                if (bitmapWidth / (float) bitmapHeight > (width / (float) height)) {
                    //宽 为主
                    isWidthBig = true;
                }
                if (bitmapWidth > width || bitmapHeight > height) {
                    if (isWidthBig) {
                        xScale = width / (float) bitmapWidth;
                        yScale = xScale;
                    } else {
                        yScale = height / (float) bitmapHeight;
                        xScale = yScale;
                    }
                }
            }
            return matrix(source, xScale, yScale);
        }
        return null;
    }

    /**
     * 尺寸缩放
     *
     * @param source 源bitmap
     * @param xScale width  缩放比例
     * @param yScale height 缩放比例
     * @return
     */
    public static Bitmap matrix(Bitmap source, float xScale, float yScale) {
        Matrix matrix = new Matrix();
        matrix.setScale(xScale, yScale);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * 高斯图片 (系统)(0-25)
     *
     * @param context
     * @param source
     * @param radius
     * @param recycle
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap source, float radius, boolean recycle) {
        Bitmap bitmap = createBitmap(source);
        //渲染脚本
        RenderScript rs = RenderScript.create(context);
        //配置高斯脚本
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blurScript.setRadius(radius);
        //输入/输出分配
        Allocation allIn = Allocation.createFromBitmap(rs, source);
        Allocation allOut = Allocation.createFromBitmap(rs, bitmap);

        blurScript.setInput(allIn);//输入
        blurScript.forEach(allOut);//输出

        allOut.copyTo(bitmap);//赋值给outBitmap
        if (recycle) {
            source.recycle();//回收
        }
        rs.destroy();//销毁
        return bitmap;

    }

    /**
     * 圆角
     *
     * @param source 原图
     * @param radius 半径
     * @return
     */
    public static Bitmap round(Bitmap source, int radius) {
        Bitmap bitmap = createBitmap(source);
        Canvas canvas = new Canvas(bitmap);//画布
        Paint paint = new Paint();//画笔
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setAntiAlias(true);//设置锯齿
        Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());//矩形
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, radius, radius, paint);//画带有圆角的图片
        //PorterDuff.Mode.SRC_OUT  rect 区域外
        //PorterDuff.Mode.SRC_IN  rect 区域内部
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));///设置相交模式
        canvas.drawBitmap(source, null, rect, paint);
        return bitmap;
    }

    /**
     * 缩略图
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    public static Bitmap thumbnail(Bitmap source, int width, int height) {
        return ThumbnailUtils.extractThumbnail(source, width, height);
    }

    /**
     * 饱和度
     *
     * @param source
     * @param saturation 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，
     *                   为1表示饱和度不变，设置大于1，就显示过饱和
     * @return
     */
    public static Bitmap saturation(Bitmap source, Float saturation) {
        return colorMatrix(source, saturation, null, null);
    }

    /**
     * 亮度 控制让*色区在色轮上旋转的角度
     *
     * @param source
     * @param lum
     * @return
     */
    public static Bitmap lum(Bitmap source, Float lum) {
        return colorMatrix(source, null, lum, null);
    }

    /**
     * 色相   红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考
     *
     * @param source
     * @param hue
     * @return
     */
    public static Bitmap hue(Bitmap source, Float hue) {
        return colorMatrix(source, null, null, hue);
    }

    public static Bitmap createBitmap(Bitmap bitmap) {
        return Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    }

    /**
     * @param source
     * @param saturation 饱和度 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，
     *                   为1表示饱和度不变，设置大于1，就显示过饱和
     * @param lum        亮度 控制让*色区在色轮上旋转的角度
     * @param hue        色相 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考
     * @return
     */
    public static Bitmap colorMatrix(Bitmap source, Float saturation, Float lum, Float hue) {
        Bitmap bitmap = createBitmap(source);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix allColorMatrix = new ColorMatrix();

        if (saturation != null) {
            ColorMatrix saturationColorMatrix = new ColorMatrix();
            saturationColorMatrix.setSaturation(saturation);
            allColorMatrix.postConcat(saturationColorMatrix);
        }

        if (lum != null) {
            ColorMatrix lumColorMatrix = new ColorMatrix();
            lumColorMatrix.setScale(lum, lum, lum, 1);//红 绿 蓝
            allColorMatrix.postConcat(lumColorMatrix);
        }

        if (hue != null) {
            // 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考
            ColorMatrix hueColorMatrix = new ColorMatrix();
            hueColorMatrix.setRotate(0, hue);//红 控制让红色区在色轮上旋转的角度
            hueColorMatrix.setRotate(1, hue);//绿
            hueColorMatrix.setRotate(2, hue);//蓝
            allColorMatrix.postConcat(hueColorMatrix);
        }

        paint.setColorFilter(new ColorMatrixColorFilter(allColorMatrix));
        canvas.drawBitmap(source, 0, 0, paint);
        return bitmap;
    }

    /**
     * 浮雕
     *
     * @param source
     * @return
     */
    public static Bitmap emboss(Bitmap source) {
        Bitmap bitmap = createBitmap(source);
        Canvas canvas = new Canvas(bitmap);
        int width = source.getWidth();
        int height = source.getHeight();
        int[] oldPixels = new int[width * height];
        int[] newPixels = new int[width * height];
        source.getPixels(oldPixels, 0, width, 0, 0, width, height);
        int color = 0;
        int pixelsR = 0;
        int pixelsG = 0;
        int pixelsB = 0;
        int color2 = 0;
        int pixelsR2 = 0;
        int pixelsG2 = 0;
        int pixelsB2 = 0;
        int pixelsA = 255;
        for (int i = 1; i < height * width; i++) {
            color = oldPixels[i - 1];
            //前一个像素
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);
            //当前像素
            color2 = oldPixels[i];
            pixelsR2 = Color.red(color2);
            pixelsG2 = Color.green(color2);
            pixelsB2 = Color.blue(color2);

            pixelsR = (pixelsR - pixelsR2 + 127);
            pixelsG = (pixelsG - pixelsG2 + 127);
            pixelsB = (pixelsB - pixelsB2 + 127);
            //均小于等于255
            if (pixelsR > 255) {
                pixelsR = 255;
            }

            if (pixelsG > 255) {
                pixelsG = 255;
            }

            if (pixelsB > 255) {
                pixelsB = 255;
            }

            newPixels[i] = Color.argb(pixelsA, pixelsR, pixelsG, pixelsB);

        }
        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap;
    }

    /**
     * 底片
     *
     * @param source
     * @return
     */
    public static Bitmap negative(Bitmap source) {
        Bitmap bitmap = createBitmap(source);
        Canvas canvas = new Canvas(bitmap);
        int width = source.getWidth();
        int height = source.getHeight();
        int[] oldPixels = new int[width * height];
        int[] newPixels = new int[width * height];
        source.getPixels(oldPixels, 0, width, 0, 0, width, height);

        int color = 0;
        int pixelsR = 0;
        int pixelsG = 0;
        int pixelsB = 0;
        int pixelsA = 0;
        for (int i = 1; i < height * width; i++) {
            color = oldPixels[i];
            //获取RGB分量
            pixelsA = Color.alpha(color);
            pixelsR = Color.red(color);
            pixelsG = Color.green(color);
            pixelsB = Color.blue(color);

            //转换
            pixelsR = (255 - pixelsR);
            pixelsG = (255 - pixelsG);
            pixelsB = (255 - pixelsB);
            //均小于等于255大于等于0
            if (pixelsR > 255) {
                pixelsR = 255;
            } else if (pixelsR < 0) {
                pixelsR = 0;
            }
            if (pixelsG > 255) {
                pixelsG = 255;
            } else if (pixelsG < 0) {
                pixelsG = 0;
            }
            if (pixelsB > 255) {
                pixelsB = 255;
            } else if (pixelsB < 0) {
                pixelsB = 0;
            }
            //根据新的RGB生成新像素
            newPixels[i] = Color.argb(pixelsA, pixelsR, pixelsG, pixelsB);

        }
        //根据新像素生成新图片
        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap;
    }

    /**
     * 叠加
     *
     * @param bgBitmap
     * @param overlayBitmap
     * @return
     */
    public static Bitmap overlay(Bitmap bgBitmap, Bitmap overlayBitmap) {
        long start = System.currentTimeMillis();
        int width = bgBitmap.getWidth();
        int height = bgBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // 对边框图片进行缩放
        int w = overlayBitmap.getWidth();
        int h = overlayBitmap.getHeight();
        float scaleX = width * 1F / w;
        float scaleY = height * 1F / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);

        Bitmap overlayCopy = Bitmap.createBitmap(overlayBitmap, 0, 0, w, h, matrix, true);

        int pixColor = 0;
        int layColor = 0;

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixA = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;
        int newA = 0;

        int layR = 0;
        int layG = 0;
        int layB = 0;
        int layA = 0;

        final float alpha = 0.5F;

        int[] srcPixels = new int[width * height];
        int[] layPixels = new int[width * height];
        bgBitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
        overlayCopy.getPixels(layPixels, 0, width, 0, 0, width, height);

        int pos = 0;
        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                pos = i * width + k;
                pixColor = srcPixels[pos];
                layColor = layPixels[pos];

                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                pixA = Color.alpha(pixColor);

                layR = Color.red(layColor);
                layG = Color.green(layColor);
                layB = Color.blue(layColor);
                layA = Color.alpha(layColor);

                newR = (int) (pixR * alpha + layR * (1 - alpha));
                newG = (int) (pixG * alpha + layG * (1 - alpha));
                newB = (int) (pixB * alpha + layB * (1 - alpha));
                layA = (int) (pixA * alpha + layA * (1 - alpha));

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                newA = Math.min(255, Math.max(0, layA));

                srcPixels[pos] = Color.argb(newA, newR, newG, newB);
            }
        }

        bitmap.setPixels(srcPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 倒影
     *
     * @param source
     * @return
     */
    public static Bitmap reflection(Bitmap source) {
        final int reflectionGap = 4;
        int width = source.getWidth();
        int height = source.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(source,
                0, height / 2, width, height / 2, matrix, false);

        Bitmap bitmap = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap,
                deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                source.getHeight(), 0, bitmap.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmap.getHeight()
                + reflectionGap, paint);
        return bitmap;
    }

    /**
     * 自处理高斯模糊
     *
     * @param bmp
     * @return
     */
    public static Bitmap selfBlur2(Bitmap bmp) {
        // 高斯矩阵
        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap newBmp = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int delta = 16; // 值越小图片会越亮，越大则越暗
        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);
                        newR = newR + pixR * gauss[idx];
                        newG = newG + pixG * gauss[idx];
                        newB = newB + pixB * gauss[idx];
                        idx++;
                    }
                }
                newR /= delta;
                newG /= delta;
                newB /= delta;
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

    /**
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap
     * @return
     * @throws OutOfMemoryError "内存不足,请降低高斯半径,获取图片像素"
     */
    public static Bitmap selfBlur1(Bitmap sentBitmap, int radius,
                                   boolean canReuseInBitmap) throws OutOfMemoryError {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();//图片宽
        int h = bitmap.getHeight();//图片高

        int[] pix = new int[w * h];//图片总点数
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);//获取总点数

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;//3+1+3  总长度
        //rgb 颜色
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];//取最大值

        int divsum = (div + 1) >> 1;//0000 1000  == 0000 0100  = 4
        divsum *= divsum;//16
        int dv[] = null;
//        try {
        dv = new int[256 * divsum];
//        } catch (OutOfMemoryError error) {
//            throw new OutOfMemoryError("内存不足,请降低高斯半径,获取图片像素");
//        }

        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static final int STYLE_X = 1;
    public static final int STYLE_Y = 2;
    public static final int STYLE_XY = 3;

    /**
     * 平铺
     *
     * @param style  方式  x,y,xy
     * @param length 宽度或长度
     * @param bitmap 平铺的图片
     * @param extra STYLE_XY时  (length 作为宽 extra作为高)
     * @return
     */
    public static Bitmap repeat(int style, int length, Bitmap bitmap, int extra) {
        if (STYLE_XY == style) {
            Bitmap repeatX = repeat(STYLE_X, length, bitmap, 0);
            return repeat(STYLE_Y, extra, repeatX, 0);
        }
        if (bitmap == null) return null;
        int tempWidth = bitmap.getWidth();
        int tempHeight = bitmap.getHeight();
        int count = 1, countWidth = 1, countHeight = 1;
        Bitmap bitmapTemp = null;
        if (STYLE_X == style) {
            countWidth = (length / tempWidth) + (length % tempHeight == 0 ? 0 : 1);
            if (countWidth <= 0) countWidth = 1;
            count = countWidth;
            bitmapTemp = Bitmap.createBitmap(length, tempHeight, Bitmap.Config.ARGB_8888);
        } else {
            countHeight = (length / tempHeight) + (length % tempHeight == 0 ? 0 : 1);
            if (countHeight <= 0) countHeight = 1;
            count = countHeight;
            bitmapTemp = Bitmap.createBitmap(tempWidth, length, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmapTemp);
        for (int i = 0; i < count; i++) {
            if (STYLE_X == style) {
                canvas.drawBitmap(bitmap, tempWidth * i, 0, null);
            } else {
                canvas.drawBitmap(bitmap, 0, tempHeight * i, null);
            }
        }
        return bitmapTemp;
    }

}
