package com.waterfairy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

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
    public static void setBg(Context context, int bgRes, ImageView bgImgView) {
        int screenWidth = ScreenInfoTool.getInstance().getScreenWidth();
        int screenHeight = ScreenInfoTool.getInstance().getScreenHeight();
        int tempScreenWidth = Math.max(screenWidth, screenHeight);
        int tempScreenHeight = Math.min(screenWidth, screenHeight);
        File file = new File(getFilePath(context, tempScreenWidth, tempScreenHeight, bgRes));
        if (file.exists()) {
            bgImgView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            bgImgView.setImageBitmap(saveAndGetBitmap(context, file.getAbsolutePath(), tempScreenWidth, tempScreenHeight, bgRes));
        }
    }

    private static Bitmap saveAndGetBitmap(Context context, String imgFilePath, int screenWidth, int screenHeight, int bgRes) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bgRes);
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

    private static String getFilePath(Context context, int screenWidth, int screenHeight, int bgRes) {
        return FileUtils.getCache(context, FileUtils.FILE_TYPE_IMG, FileUtils.CACHE_TYPE_SD) +
                "/bg_img_" + bgRes + "_" + screenWidth + "_" + screenHeight + ".jpg";
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
}
