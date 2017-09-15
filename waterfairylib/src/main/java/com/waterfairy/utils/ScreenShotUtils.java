package com.waterfairy.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/9/15
 * @Description:
 */

public class ScreenShotUtils {
    private static boolean isInThread;

    public static void shot(View view, OnScreenShotListener onScreenShotListener) {
        shot(view, null, 0, 0, 0, 0, onScreenShotListener);
    }

    public static void shot(View view, String saveFilePath, OnScreenShotListener onScreenShotListener) {
        shot(view, saveFilePath, 0, 0, 0, 0, onScreenShotListener);
    }

    public static void shot(View view, int fromX, int fromY, OnScreenShotListener onScreenShotListener) {
        shot(view, null, fromX, fromY, 0, 0, onScreenShotListener);
    }

    public static void shot(View view, String saveFilePath, int fromX, int fromY, OnScreenShotListener onScreenShotListener) {
        shot(view, saveFilePath, fromX, fromY, 0, 0, onScreenShotListener);
    }

    public static void shot(final View shotView, final String shotFileName,
                            final int fromX, final int fromY,
                            final int width, final int height,
                            final OnScreenShotListener onScreenShotListener) {
        if (isInThread) {
            returnError("截屏中,请稍候...", onScreenShotListener);
        } else {
            new AsyncTask<Void, Void, Object[]>() {
                @Override
                protected Object[] doInBackground(Void... params) {
                    isInThread = true;
                    long time = System.currentTimeMillis();
                    Object[] state = shotInThread(shotView, shotFileName, fromX, fromY, width, height);
                    Log.i("ScreenShotUtils", "shot time :  " + (System.currentTimeMillis() - time));
                    isInThread = false;
                    return state;
                }

                @Override
                protected void onPostExecute(Object[] result) {
                    super.onPostExecute(result);
                    if ((Boolean) result[0]) {
                        returnSuccess((String) result[1], onScreenShotListener);
                    } else {
                        returnError((String) result[1], onScreenShotListener);
                    }
                }
            }.execute();
        }

    }

    private static Object[] shotInThread(
            View shotView, String shotFilePath,
            int fromX, int fromY,
            int width, int height) {


        if (shotView == null) {
            return new Object[]{false, "view 为空"};
        }
        if (TextUtils.isEmpty(shotFilePath)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
            File screenShots = Environment.getExternalStoragePublicDirectory("ScreenShots");
            if (!screenShots.exists()) {
                screenShots.mkdirs();
            }
            shotFilePath = screenShots.getAbsolutePath() + "/" + sdf.format(new Date()) + ".png";
        } else {
            File file = new File(new File(shotFilePath).getParent());
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        File saveFile = new File(shotFilePath);
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return new Object[]{false, "文件创建失败"};
            }
        }

        Bitmap bitmap = null;
        try {
            shotView.setDrawingCacheEnabled(true);
            shotView.buildDrawingCache();
            bitmap = shotView.getDrawingCache();
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[]{false, "截图失败"};
        }
        if (bitmap != null) {
            try {
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                if (width > 0 && height > 0) {
                    fromX = fromX < 0 ? 0 : fromX;
                    fromY = fromY < 0 ? 0 : fromY;
                    if (fromX >= bitmapWidth || fromY >= bitmapHeight) {
                        return new Object[]{false, "起始位置超过图片区域"};
                    }
                    if (fromX + width >= bitmapWidth) {
                        width = bitmapWidth - fromX;
                    }
                    if (fromY + height >= bitmapHeight) {
                        height = bitmapHeight - fromY;
                    }
                    bitmap = Bitmap.createBitmap(bitmap, fromX, fromY, width, height);
                } else if (fromX > 0 || fromY > 0) {
                    if (fromX >= bitmapWidth || fromY >= bitmapHeight) {
                        return new Object[]{false, "起始位置超过图片区域"};
                    }
                    bitmap = Bitmap.createBitmap(bitmap, fromX, fromY, bitmapWidth - fromX, bitmapHeight - fromY);
                }
                FileOutputStream out = new FileOutputStream(shotFilePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                bitmap.recycle();
                shotView.destroyDrawingCache();
                return new Object[]{true, shotFilePath};
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shotView.destroyDrawingCache();
        return new Object[]{false, "截图失败"};
    }

    private static void returnSuccess(String shotFilePath, OnScreenShotListener onScreenShotListener) {
        if (onScreenShotListener != null) onScreenShotListener.onShotSuccess(shotFilePath);
    }


    private static void returnError(String error, OnScreenShotListener onScreenShotListener) {
        if (onScreenShotListener != null) onScreenShotListener.onShotError(error);
    }

    public interface OnScreenShotListener {
        void onShotSuccess(String shotPath);

        void onShotError(String error);
    }

    public static int getStatusBarHeight(Context context) {
        if (context == null) return 0;
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight(Context context) {
        if (context == null) return 0;
        TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float dimension = actionbarSizeTypedArray.getDimension(0, 0);
        actionbarSizeTypedArray.recycle();
        return (int) dimension;
    }

}
