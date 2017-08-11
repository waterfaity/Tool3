package com.waterfairy.tool3;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cloud.classroom.audiorecord.lame.RecMicToMp3;

import java.util.Date;

public class RecordActivity extends AppCompatActivity {
    private static final String TAG = "RecordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        final RecMicToMp3 micToMp3 = new RecMicToMp3("/sdcard/jjjj.mp3", 16000);
        micToMp3.setHandle(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "handleMessage: " + msg.obj);
            }
        });
        micToMp3.start();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                micToMp3.stop();
            }
        };
        handler.sendEmptyMessageDelayed(0, 10000);
    }
}
