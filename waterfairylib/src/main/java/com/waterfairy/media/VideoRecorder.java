package com.waterfairy.media;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

import com.waterfairy.utils.PermissionUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/21
 * @Description:
 */

public class VideoRecorder {
    public static final int ERROR_NO_SDCARD = 100;//没有sd卡
    public static final int ERROR_NO_PERMISSION_STORAGE = 101;//没有相机权限
    public static final int ERROR_NO_PERMISSION_CAMERA = 102;//没有相机权限
    public static final int ERROR_NO_PERMISSION_RECORD = 103;//没有录音权限
    public static final int ERROR_NO_SURFACE_VIEW = 104;//没有录音权限
    public static final int ERROR_INIT_RECORDER = 105;//未初始化视频录制
    public static final int ERROR_NO_FILE = 106;//未初始化视频录制

    public static final int WORM_NO_RECORD = 201;//没有录制
    public static final int WORM_RECORDING = 202;//录制中

    public static final int NORMAL_BIT_RATE = 8 * 1024 * 1024;
    public static final int NORMAL_WIDTH = 1280;
    public static final int NORMAL_HEIGHT = 720;
    public static final int NORMAL_RATE = 20;

    private static VideoRecorder VIDEO_RECORDER;
    private OnVideoRecordListener onVideoRecordListener;
    //系统媒体录制
    private MediaRecorder mMediaRecorder;
    private int mWidth = NORMAL_WIDTH;//视频宽高
    private int mHeight = NORMAL_HEIGHT;
    private int mRate = NORMAL_RATE;
    private int mBitRate = NORMAL_BIT_RATE;
    private SurfaceView mSurfaceView;//预览
    private String mVideoPath;
    private boolean isRecording;
    private boolean canRecord;
    private Context context;

    public static VideoRecorder getInstance() {
        if (VIDEO_RECORDER == null) {
            VIDEO_RECORDER = new VideoRecorder();
        }
        return VIDEO_RECORDER;
    }

    public boolean isRecording() {
        return isRecording;
    }

    /**
     * @param videoPath
     * @param width
     * @param height
     * @param rate
     * @param bitRate
     * @param surfaceView
     * @param onVideoRecordListener
     */
    public void initVideoRecorder(Application application, String videoPath,
                                  int width, int height,
                                  int rate, int bitRate,
                                  SurfaceView surfaceView,
                                  OnVideoRecordListener onVideoRecordListener) {
        this.context = application.getApplicationContext();
        mVideoPath = videoPath;
        mWidth = width;
        mHeight = height;
        mRate = rate;
        mBitRate = bitRate;
        mSurfaceView = surfaceView;
        this.onVideoRecordListener = onVideoRecordListener;
    }

    public void checkAndRecord(Activity activity) {
        if (checkState(activity)) {
            if (isRecording) {
                if (onVideoRecordListener != null)
                    onVideoRecordListener.onRecordWarm(WORM_RECORDING, "录制中");
            } else {
                startRecord();
            }
        }
    }

    /**
     *
     */
    public void startRecord() {
        if (!canRecord) {
            return;
        }
        // 创建MediaPlayer对象
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera != null) {
            //方向
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(0);
                mMediaRecorder.setOrientationHint(0);
            } else {
                mMediaRecorder.setOrientationHint(90);
                camera.setDisplayOrientation(90);
            }
            Camera.Parameters parameters = camera.getParameters();
            String s = parameters.get("preview-size");
            Log.i("test", "startRecord: " + s);
            camera.cancelAutoFocus();//自动对焦
            camera.unlock();

        }
        mMediaRecorder.setCamera(camera);
        isRecording = false;
        // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置摄像头获取图像
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //文件输出格式
        //必须在设置声音编码之前设置
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //设置声音编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置图像编码
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        //
//        mMediaRecorder.setVideoEncodingBitRate(mBitRate);
        //视频宽高
//        mMediaRecorder.setVideoSize(mWidth, mHeight);
        //帧
//        mMediaRecorder.setVideoFrameRate(mRate);
        //预览
        if (mSurfaceView == null) {
            if (onVideoRecordListener != null) {
                onVideoRecordListener.onRecordError(ERROR_NO_SURFACE_VIEW, "没有设置预览");
            }
            return;
        }
        mSurfaceView.getHolder();
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        //设置输出文件
        mMediaRecorder.setOutputFile(mVideoPath);
        //准备
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            if (onVideoRecordListener != null)
                onVideoRecordListener.onRecordError(ERROR_INIT_RECORDER, "录制初始化失败");
            return;
        }
        //开始录制
        mMediaRecorder.start();
        if (onVideoRecordListener != null) onVideoRecordListener.onRecordStart();
        isRecording = true;

    }


    public boolean checkState(Activity activity) {
        canRecord = false;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (onVideoRecordListener != null)
                onVideoRecordListener.onRecordError(ERROR_NO_SDCARD, "请插入内存卡");
            canRecord = false;
        } else {
            if (!PermissionUtils.requestPermission(activity, PermissionUtils.REQUEST_STORAGE)) {
                if (onVideoRecordListener != null)
                    onVideoRecordListener.onRecordError(ERROR_NO_PERMISSION_STORAGE, "请开启内存卡读写权限");
                canRecord = false;
            } else {
                if (!PermissionUtils.requestPermission(activity, PermissionUtils.REQUEST_CAMERA)) {
                    if (onVideoRecordListener != null)
                        onVideoRecordListener.onRecordError(ERROR_NO_PERMISSION_CAMERA, "请开启相机权限");
                    canRecord = false;
                } else {
                    if (!PermissionUtils.requestPermission(activity, PermissionUtils.REQUEST_RECORD)) {
                        if (onVideoRecordListener != null)
                            onVideoRecordListener.onRecordError(ERROR_NO_PERMISSION_RECORD, "请开启录音权限");
                        canRecord = false;
                    } else {
                        if (!new File(mVideoPath).exists()) {
                            if (onVideoRecordListener != null)
                                onVideoRecordListener.onRecordError(ERROR_NO_FILE, "请先创建视频文件");
                            canRecord = false;
                        } else {
                            canRecord = true;
                        }
                    }
                }
            }
        }
        if (onVideoRecordListener != null)
            onVideoRecordListener.onRecordChecked(canRecord);
        return true;
    }


    public void stopRecord() {
        if (isRecording) {
            if (mMediaRecorder != null) {
                isRecording = false;
                // 停止录制
                mMediaRecorder.stop();
                // 释放资源
                mMediaRecorder.release();
                mMediaRecorder = null;
                if (onVideoRecordListener != null) onVideoRecordListener.onRecordStop();
            } else {
                if (onVideoRecordListener != null)
                    onVideoRecordListener.onRecordError(ERROR_INIT_RECORDER, "未初始化视频录制");
            }
        } else {
            if (onVideoRecordListener != null)
                onVideoRecordListener.onRecordWarm(WORM_NO_RECORD, "未开始录制");
        }
    }

    public void setOnVideoRecordListener(OnVideoRecordListener onVideoRecordListener) {
        this.onVideoRecordListener = onVideoRecordListener;
    }

    public interface OnVideoRecordListener {
        void onRecordChecked(boolean canRecord);

        void onRecordStart();

        void onRecordStop();

        void onRecordError(int code, String message);

        void onRecordWarm(int code, String message);

    }

}
