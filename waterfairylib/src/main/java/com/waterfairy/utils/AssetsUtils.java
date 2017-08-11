package com.waterfairy.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by water_fairy on 2017/3/30.
 */

public class AssetsUtils {
//    第一种方法：
//    String path = "file:///android_asset/文件名";
//
//    第二种方法：
//    InputStream abpath = getClass().getResourceAsStream("/assets/文件名");

    private static final String TAG = "assetUtils";

    /**
     * @param context
     * @param assetPath  例如html   asset/html  如果  assetPath =null  copy所有文件
     * @param targetPath
     */
    public static void copyPath(Context context, String assetPath, @NonNull String targetPath) throws IOException {
        String[] paths = null;
        paths = TextUtils.isEmpty(assetPath) ?
                context.getAssets().getLocales() :
                context.getAssets().list(assetPath);
        save(context, paths, assetPath, targetPath);
    }

    private static void save(Context context, String[] paths, String srcPath, String targetPath) throws IOException {
        if (TextUtils.isEmpty(targetPath)) {
            throw new IOException("targetPath file is null");
        }
        File file = new File(targetPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            throw new IOException("targetPath file mk error");
        }

        if (paths != null && paths.length != 0) {
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                String tempSrcPath = srcPath + "/" + path;//asset 中 ,  html + css
                String tempTargetPath = targetPath + "/" + path;//文件中  /html +css
                String[] list = context.getAssets().list(tempSrcPath);
                if (list.length == 0) {
                    //文件
                    save(context, tempSrcPath, tempTargetPath);
                } else {
                    //文件夹
                    save(context, list, tempSrcPath, tempTargetPath);
                }
            }
        } else {
            throw new IOException("no files");
        }
    }

    private static void save(Context context, String tempSrcPath, String tempTargetPath) {
        try {
            InputStream is = context.getAssets().open(tempSrcPath);
            File file = new File(tempTargetPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.exists()) {
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024 * 512];
                int len;
                while ((len = is.read(buf)) > 0)
                    outputStream.write(buf, 0, len);
                is.close();
                outputStream.close();
            } else {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context context
     * @param path assets文件
     * @return
     * @throws IOException
     */

    public static synchronized String getText(Context context, String path) throws IOException {
        InputStream is = getIS(context, path);
        return isToString(is);

    }

    /**
     * @param context context
     * @param path assets文件
     * @return InputStream
     * @throws IOException
     */
    public static synchronized InputStream getIS(Context context, String path) throws IOException {
        if (context == null) throw new IOException("context is null");
        if (TextUtils.isEmpty(path)) throw new IOException("assets path is null");
        return context.getAssets().open(path);
    }

    /**
     * @param inputStream is
     * @return String
     * @throws IOException
     */
    public static synchronized String isToString(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(isr);
        return bufferedReader.readLine();
    }

}
