package com.waterfairy.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by water_fairy on 2016/12/5.
 */

public class FileUtils {
    /**
     * @param context
     * @param filePath img/jpg(前面不需要/)
     * @return
     */
    public static String getExtraPackageFilePath(Context context, String filePath) {

        String mainPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + context.getPackageName();
        String path = mainPath + "/" + filePath;
        File file = new File(path);
        boolean mkdirs = file.mkdirs();
        return path;

    }

    public static String getExtraStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * @param context
     * @param filePath img/jpg(前面不需要/)
     * @param fileName 文件名字
     * @return
     */
    @RequiresPermission("android.permission.WRITE_EXTERNAL_STORAGE")
    public static File createExtraPackageFile(Context context, String filePath, String fileName) {

        File file = new File(getExtraPackageFilePath(context, filePath));
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                File tempFile = new File(file.getPath() + "/" + fileName);
                if (!tempFile.exists()) {
                    try {
                        tempFile.createNewFile();
                        return tempFile;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            throw new Exception("创建文件失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean copyFile(String fromFile, String toFile) throws IOException {
        File saveFile = new File(toFile);
        File parentFile = new File(saveFile.getParent());
        boolean canSave = false;
        canSave = parentFile.exists() || parentFile.mkdirs();
        if (canSave) {
            canSave = saveFile.exists() || saveFile.createNewFile();
            if (canSave) {
                InputStream inputStream = new FileInputStream(fromFile);
                OutputStream outputStream = new FileOutputStream(saveFile);
                byte[] buf = new byte[1024 * 1024];
                int len = 0;
                while ((len = inputStream.read(buf)) > 0)
                    outputStream.write(buf, 0, len);
                inputStream.close();
                outputStream.close();
                return true;
            }
        }
        return false;
    }

    public static String getAPPPath(Context context, String extra) {
        String cachePath = context.getCacheDir().getAbsolutePath();
        return cachePath.substring(0, cachePath.length() - 6) +
                (TextUtils.isEmpty(extra) ? "" : "/" + extra);
    }

}
