package com.waterfairy.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

/**
 * Created by water_fairy on 2016/7/29.
 */
public class PermissionUtils {

    /**
     * 位置权限,定位/蓝牙
     */
    public final static int REQUEST_LOCATION = 1;

    /**
     * 文件读写
     */
    public final static int REQUEST_STORAGE = 2;

    /**
     * 相机
     */
    public final static int REQUEST_CAMERA = 3;

    /**
     * 录音
     */
    public final static int REQUEST_RECORD = 4;

    /**
     * 申请权限
     *
     * @param activity Activity
     * @param request  请求类型
     */
    public static void requestPermission(Activity activity, int request) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        String[] permissions = null;
        String permission = null;
        switch (request) {
            case REQUEST_LOCATION:
                permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
                permission = Manifest.permission.ACCESS_COARSE_LOCATION;
                break;
            case REQUEST_CAMERA:
                permissions = new String[]{Manifest.permission.CAMERA};
                permission = Manifest.permission.CAMERA;
                break;
            case REQUEST_STORAGE:
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE};
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            case REQUEST_RECORD:
                permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                permission = Manifest.permission.RECORD_AUDIO;
                break;
        }
        if (permissions != null) {
            requestPermission(activity, permissions, permission, request);
        }
    }

    /**
     * @param activity    activity
     * @param permissions 权限组
     * @param permission  权限
     * @param request     requestCode  Activity中 会返回权限申请状态(类似startActivityForResult)
     */

    public static void requestPermission(Activity activity,
                                         @NonNull String[] permissions,
                                         @NonNull String permission,
                                         int request) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        int permissionCode = checkPermission(activity, permission);
        if (permissionCode != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, request);
        }
    }

    /**
     * 检查权限
     *
     * @param activity   activity
     * @param permission 某个权限
     * @return {
     */
    public static int checkPermission(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission);
    }

}
