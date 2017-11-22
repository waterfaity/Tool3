package com.waterfairy.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
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

    private static final String TAG = "fileUtils";

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
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return null;
                        }
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

    /**
     * @param fromPath
     * @param toPath       /sdcard/test
     * @param createToPath 是否创建根目录
     * @param deleteFrom   copy后删除
     * @throws Exception
     */
    public static void copyPath(String fromPath, String toPath, boolean createToPath, boolean deleteFrom) throws Exception {
        File fromFile = new File(fromPath);
        if (!fromFile.exists()) {
            throw new Exception("源文件夹不存在");
        }
        if (!fromFile.isDirectory()) {
            throw new Exception("源地址非文件夹路径地址");
        }
        File file = new File(toPath);
        boolean canCopy = true;
        if (!file.isDirectory()) {
            throw new Exception("目标地址非文件夹地址");
        } else if (!file.exists()) {
            canCopy = createToPath && file.mkdirs();
        }
        if (!canCopy) {
            throw new Exception("拷贝失败");
        } else {
            copyPath(fromFile.getAbsolutePath(), toPath, deleteFrom);
        }
    }

    private static void copyPath(String fromPath, String toPath, boolean deleteFrom) throws Exception {
        File fromFile = new File(fromPath);
        if (fromFile.exists()) {
            File[] files = fromFile.listFiles();
            if (files.length > 0) {
                for (File childFile : files) {
                    String copyFilePath = new File(toPath, childFile.getName()).getAbsolutePath();
                    if (childFile.isDirectory()) {
                        //路径
                        //copy 文件夹
                        File file = new File(toPath, childFile.getName());
                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                throw new Exception("拷贝文件夹失败:" + file.getAbsolutePath());
                            }
                        }
                        //copy文件夹下的文件
                        copyPath(childFile.getAbsolutePath(), copyFilePath, deleteFrom);
                    } else {
                        //文件
                        try {
                            copyFile(childFile.getAbsolutePath(), copyFilePath);
                            //删除文件
                            if (deleteFrom)
                                childFile.delete();
                        } catch (Exception e) {
                            throw new Exception("拷贝文件失败:" + childFile.getAbsolutePath());
                        }
                    }
                }
            }
            //删除文件夹
            if (deleteFrom) fromFile.delete();
        }

    }

    public static void copyFile(String fromFile, String toFile) throws IOException {
        File saveFile = new File(toFile);
        File parentFile = new File(saveFile.getParent());
        boolean canSave = parentFile.exists() || parentFile.mkdirs();
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
            }
        }
    }

    public static String getAPPPath(Context context, String extra) {
        String cachePath = context.getCacheDir().getAbsolutePath();
        return cachePath.substring(0, cachePath.length() - 6) +
                (TextUtils.isEmpty(extra) ? "" : "/" + extra);
    }

    /**
     * @param delFile
     */
    public static void deleteFile(File delFile) throws Exception {
        if (delFile.exists()) {
            if (delFile.isDirectory()) {
                File[] files = delFile.listFiles();
                if (files.length > 0) {
                    for (File childFile : files) {
                        deleteFile(childFile);
                    }
                }
            }
            boolean delete = delFile.delete();
            if (!delete) throw new Exception("删除异常" + delFile.getAbsolutePath());
        }
    }
    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (createPath(file.getParentFile().getAbsolutePath())) {
                try {
                    return file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public static boolean createPath(String path) {
        File parentFile = new File(path);
        return (parentFile.exists() || parentFile.mkdirs());
    }

}
