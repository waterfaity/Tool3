package com.waterfairy.apkLoader;

import android.app.Application;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by water_fairy on 2017/8/23.
 */

public class ApkLoader {
    private static ApkLoader apkLoader;

    private ApkLoader() {

    }

    public static ApkLoader getInstance() {
        if (apkLoader == null) {
            apkLoader = new ApkLoader();
        }
        return apkLoader;
    }

    /**
     * 获取classLoader
     *
     * @param application
     * @param apkPath
     * @return
     */
    public DexClassLoader loadApk(Application application, String apkPath) {
        DexClassLoader classLoader = null;
        try {
            File dexOutputDir = application.getDir("dex", 0);
            String dexOutputDirPath = dexOutputDir.getAbsolutePath();
            ClassLoader appClassLoader = application.getClassLoader();
            classLoader = new DexClassLoader(apkPath, dexOutputDirPath, null, appClassLoader);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return classLoader;
    }

    /**
     * 加载指定名称的类
     *
     * @param className 类名(包含包名)
     */
    public Object loadClass(DexClassLoader dexClassLoader, String className) {
        if (dexClassLoader == null) {
            return null;
        }
        try {
            Class<?> clazz = dexClassLoader.loadClass(className);
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
