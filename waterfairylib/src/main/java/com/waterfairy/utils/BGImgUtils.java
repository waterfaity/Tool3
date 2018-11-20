package com.waterfairy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/3 14:17
 * @info:
 */
public class BGImgUtils {
    /**
     * 根据屏幕尺寸是指背景大小
     * 4:3 16:9 16:10 18:9
     *
     * @param bgRes
     * @param bgImgView
     */
    public static void setBg(Context context, int bgRes, String imgName, ImageView bgImgView) {
        if (bgRes == 0) return;
        if (bgImgView == null) return;
        int screenWidth = ScreenInfoTool.getInstance().getScreenWidth();
        int screenHeight = ScreenInfoTool.getInstance().getScreenHeight();
        int tempScreenWidth = Math.max(screenWidth, screenHeight);
        int tempScreenHeight = Math.min(screenWidth, screenHeight);
        File file = new File(getFilePath(context, tempScreenWidth, tempScreenHeight, imgName));
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bgImgView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath(), options));
        } else {
            bgImgView.setImageBitmap(saveAndGetBitmap(context, file.getAbsolutePath(), tempScreenWidth, tempScreenHeight, bgRes));
        }
    }

    private static Bitmap saveAndGetBitmap(Context context, String imgFilePath, int screenWidth, int screenHeight, int bgRes) {
        Bitmap bitmap = null;
        try {
//            bitmap = ImageUtils.decodeFromRes(context, bgRes, screenWidth, screenHeight, true);
            Bitmap bitmapTemp = ImageUtils.decodeFromRes(context, bgRes, screenWidth, screenHeight, true);
            bitmap = ImageUtils.matrix(bitmapTemp, screenWidth, screenHeight, false);
            if (bitmap != bitmapTemp) {
                bitmapTemp.recycle();
                bitmapTemp = null;
            }
//            gradlew compileDebugSources --stacktrace -info
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(), bgRes);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (screenWidth / (float) screenHeight >= width / (float) height) {
            //屏幕宽 截取图片上部分
            int tempHeight = (int) (width * (screenHeight / (float) screenWidth));
            Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, 0, height - tempHeight, width, tempHeight);
            saveBitmap(bitmapTemp, imgFilePath);
            return bitmapTemp;
        } else {
            //图片宽 截取图片宽中间
            int tempWidth = (int) (height * (screenWidth / (float) screenHeight));
            Bitmap bitmapTemp = Bitmap.createBitmap(bitmap, (width - tempWidth) / 2, 0, tempWidth, height);
            saveBitmap(bitmapTemp, imgFilePath);
            return bitmapTemp;
        }
    }

    private static String getFilePath(Context context, int screenWidth, int screenHeight, String imgName) {
        return FileUtils.getCache(context, FileUtils.FILE_TYPE_IMG, FileUtils.CACHE_TYPE_SD) +
                "/bg_img_" + imgName + "_" + screenWidth + "_" + screenHeight + ".jpg";
    }

    private static void saveBitmap(final Bitmap bitmap, final String savePath) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ImageUtils.saveBitmap(savePath, bitmap);
                return null;
            }
        }.execute();
    }

    /**
     * 图片销毁
     * 注:如果用 setImageRes()    销毁 再 使用  ,提示报错 isRecycled
     *
     * @param view
     */
    public static void destroy(View view) {
        if (view != null) {
            Drawable drawable = null;
            if (view instanceof ImageView) {
                drawable = ((ImageView) view).getDrawable();
            } else {
                drawable = view.getBackground();
            }
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }
    }
}
