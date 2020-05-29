package com.waterfairy.tool3.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.waterfairy.media.VideoRecorder;
import com.waterfairy.tool3.MyApp;
import com.waterfairy.tool3.R;
import com.waterfairy.utils.FileUtils;
import com.waterfairy.utils.ToastUtils;

public class VideoRecordActivity extends AppCompatActivity implements VideoRecorder.OnVideoRecordListener, View.OnClickListener {

    private static final String TAG = "videoRecord";
    private VideoRecorder videoRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        videoRecorder = VideoRecorder.getInstance();
        String filePath = "/sdcard/waterFairy/video/record.mp4";
        FileUtils.createFile(filePath);
        videoRecorder.initVideoRecorder(
                MyApp.getApp(),
                filePath,
                VideoRecorder.NORMAL_WIDTH,
                VideoRecorder.NORMAL_HEIGHT,
                VideoRecorder.NORMAL_RATE,
                VideoRecorder.NORMAL_BIT_RATE,
                (SurfaceView) findViewById(R.id.surface_view),
                this
        );
        videoRecorder.checkState(this);

    }

    public void onRecordChecked(boolean canRecord) {
        Log.i(TAG, "onRecordChecked: " + canRecord);
    }

    @Override
    public void onRecordStart() {
        ToastUtils.show("录制开始");
    }

    @Override
    public void onRecordStop() {
        ToastUtils.show("录制停止");
    }

    @Override
    public void onRecordError(int code, String message) {
        ToastUtils.show(message);
        Log.i(TAG, "onRecordError: " + message);
    }

    @Override
    public void onRecordWarm(int code, String message) {
        ToastUtils.show(message);
        Log.i(TAG, "onRecordWarm: " + message);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.start) {
            if (!videoRecorder.isRecording()) {
                videoRecorder.startRecord();
            }
        } else {
            if (videoRecorder.isRecording()) {
                videoRecorder.stopRecord();
            }
        }
    }
}
