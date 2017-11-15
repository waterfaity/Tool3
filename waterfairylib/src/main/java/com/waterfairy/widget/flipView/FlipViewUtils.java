package com.waterfairy.widget.flipView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.waterfairy.utils.ImageUtils;
import com.waterfairy.utils.MD5Utils;

import java.io.File;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/10/25
 * @Description:
 */

public class FlipViewUtils {

    private static final String TAG = "FlipViewUtils";
    private static String defaultCachePath;
    private static boolean canCache;

    /**
     * 缓存路径
     *
     * @param path
     */
    public static void initCachePath(String path) {
        defaultCachePath = path;
        File file = new File(path);
        canCache = file.exists() || file.mkdirs();
    }

    public static String getCachePath(String imgFilePath) {
        if (canCache) {
            return new File(defaultCachePath, MD5Utils.getMD5Code(imgFilePath)).getAbsolutePath();
        } else {
            File file = new File(imgFilePath);
            return new File(file.getParent(), file.getName() + "-cache").getAbsolutePath();
        }
    }

    /**
     * 获取 本地图片 缓存图片
     *
     * @param imgFilePath 本地路径
     * @param maxWidth    最大宽
     * @param maxHeight   最大高
     * @return 返回bitmap
     */
    public static Bitmap getBitmap(String imgFilePath, int maxWidth, int maxHeight) {
        long l = System.currentTimeMillis();
        String cacheFilePath = getCachePath(imgFilePath);
        Bitmap showBitmap = null;
        if (new File(cacheFilePath).exists()) {
            //获取缓存
            showBitmap = BitmapFactory.decodeFile(cacheFilePath);
        }
        if (showBitmap == null && new File(imgFilePath).exists()) {
            //获取非缓存-并生成缓存
            showBitmap = BitmapFactory.decodeFile(imgFilePath);
            showBitmap = ImageUtils.matrix(showBitmap, maxWidth, maxHeight, false);
            saveImg(showBitmap, cacheFilePath);
        }
        if (showBitmap != null) {
//            Log.i(TAG, "getBitmap:获取图片用时: " + (System.currentTimeMillis() - l));
            return showBitmap;
        } else {
//            Log.i(TAG, "获取图片失败:" + imgFilePath);
        }
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

    private static void saveImg(final Bitmap showBitmap, final String cacheFilePath) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (showBitmap != null) {
//                    long l = System.currentTimeMillis();
                    ImageUtils.saveBitmap(cacheFilePath, showBitmap);
//                    Log.i(TAG, "run: 保存图片 " + showBitmap.getWidth() + "*" + showBitmap.getHeight() + "  用时:" + (System.currentTimeMillis() - l));
                }
            }
        }.start();
    }
}
