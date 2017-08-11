package com.waterfairy.tool3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.waterfairy.media.Mp3Player;
import com.waterfairy.utils.ToastUtils;

public class Mp3Activity extends AppCompatActivity implements Mp3Player.onMp3PlayListener {
    private Mp3Player mp3Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3);
        mp3Player = new Mp3Player();
        mp3Player.setOnMp3PlayListener(this);
    }


    public void play(View view) {
        mp3Player.play("/sdcard/jj.mp3");
    }

    public void pause(View view) {
        mp3Player.pause();
    }

    public void stop(View view) {
        mp3Player.stop();
    }

    public void release(View view) {
        mp3Player.release();
    }

    public void replay(View view) {
        mp3Player.resume();
    }

    public void seek(View view) {
        mp3Player.seekTo(10000);
    }

    @Override
    public void onMp3PlayError(int state, String message) {
        ToastUtils.show("error:" + message);
    }

    @Override
    public void onPlayStateChanged(int state, String message) {
        ToastUtils.show(message);
    }
}
