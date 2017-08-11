package com.waterfairy.media;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by water_fairy on 2017/8/11.
 * 995637517@qq.com
 */

public class Mp3Player {
    private static final String TAG = "mp3Player";
    private MediaPlayer mediaPlayer;//播放器
    private onMp3PlayListener onMp3PlayListener;//播放监听
    private String currentPath;//当前播放的文件路径
    //错误
    public static final int ERROR_NOT_INIT = 101;//未初始化
    public static final int ERROR_NOT_EXIST = 102;//文件不存在
    public static final int ERROR_HAS_STOP = 103;//已经停止
    public static final int ERROR_PLAY = 104;//播放错误

    private int mediaState;//播放器状态
    public static final int NOT_INIT = 0;
    public static final int PREPARED = 1;
    public static final int ERROR = 2;

    private int playState;//播放状态
    public static final int PLAYING = 11;
    public static final int PAUSE = 12;
    public static final int STOP = 13;
    public static final int RELEASE = 14;
    public static final int COMPLETE = 15;


    public Mp3Player() {
        initMP3();
    }

    /**
     * 设置监听
     *
     * @param onMp3PlayListener
     */
    public void setOnMp3PlayListener(onMp3PlayListener onMp3PlayListener) {
        this.onMp3PlayListener = onMp3PlayListener;
    }

    /**
     * 初始化播放器
     */
    private void initMP3() {
        mediaPlayer = new MediaPlayer();
        currentPath = "";

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaState = PREPARED;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion: ");
                if (onMp3PlayListener != null)
                    onMp3PlayListener.onPlayStateChanged(COMPLETE, "播放结束");
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaState = ERROR;
                String message = "播放错误";
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        message = "音频文件格式错误";
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        message = "媒体服务停止工作";
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        message = "媒体播放器错误";
                        break;
                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        message = "音频文件不支持拖动播放";
                        break;
                }
                Log.i(TAG, "onError: " + what + "--" + extra);
                if (onMp3PlayListener != null)
                    onMp3PlayListener.onMp3PlayError(ERROR_PLAY, message);
                release();
                return false;
            }
        });
    }

    /**
     * 播放
     *
     * @param mediaPath
     */
    public void play(String mediaPath) {
        play(mediaPath, -1);
    }

    /**
     * 播放
     *
     * @param mediaPath
     * @param time      播放位置  毫秒
     */
    public void play(String mediaPath, int time) {
        File file = new File(mediaPath);
        if (!file.exists()) {
            if (onMp3PlayListener != null)
                onMp3PlayListener.onMp3PlayError(ERROR_NOT_EXIST, "文件不存在");
            mediaState = ERROR;
            return;
        }
        if (!checkInit()) {
            initMP3();
        }
        try {
            if (!TextUtils.equals(mediaPath, currentPath)) {
                mediaPlayer.setDataSource(mediaPath);
                mediaPlayer.prepare();
            }
            if (playState == STOP) {
                mediaPlayer.prepare();
            }
            this.currentPath = mediaPath;
            if (time >= 0) {
                mediaPlayer.seekTo(time);
            } else {
                mediaPlayer.start();
                playState = PLAYING;
                if (onMp3PlayListener != null)
                    onMp3PlayListener.onPlayStateChanged(PLAYING, "播放中");
            }
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            mediaState = PREPARED;
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (checkInit() && !checkStop()) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playState = PAUSE;
                if (onMp3PlayListener != null)
                    onMp3PlayListener.onPlayStateChanged(PAUSE, "暂停");
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (checkInit()) {
            mediaPlayer.stop();
            playState = STOP;
            if (onMp3PlayListener != null)
                onMp3PlayListener.onPlayStateChanged(STOP, "停止");
        }
    }

    /**
     * 播放指定位置
     *
     * @param time
     */
    public void seekTo(int time) {
        if (checkInit() && !checkStop()) {
            play(currentPath, time);
        }
    }

    /**
     * 释放
     */
    public void release() {
        if (checkInit()) {
            mediaPlayer.release();
            mediaPlayer = null;
            playState = RELEASE;
            mediaState = NOT_INIT;
            if (onMp3PlayListener != null) onMp3PlayListener.onPlayStateChanged(RELEASE, "关闭");
        }
    }

    /**
     * 恢复播放播放
     */
    public void resume() {
        if (checkInit() && !checkStop()) {
            play(currentPath, -1);
        }
    }

    /**
     * 是否停止
     *
     * @return
     */
    private boolean checkStop() {
        if (playState == STOP) {
            if (onMp3PlayListener != null)
                onMp3PlayListener.onMp3PlayError(ERROR_HAS_STOP, "播放已停止");
            return true;
        } else {
            return false;
        }
    }

    public int getPlayState() {
        return playState;
    }

    public int getMediaState() {
        return mediaState;
    }

    /**
     * 是否初始化
     *
     * @return
     */
    private boolean checkInit() {
        if (mediaPlayer == null) {
            if (onMp3PlayListener != null)
                onMp3PlayListener.onMp3PlayError(ERROR_NOT_INIT, "播放器未初始化");
            return false;
        }
        return true;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 播放进度 百分比
     *
     * @return
     */
    public float getPlayRatio() {
        if (checkInit()) {
            int duration = mediaPlayer.getDuration();
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (duration == 0) return 0;
            else {
                return (float) currentPosition / duration;
            }
        }
        return 0;
    }

    public static interface onMp3PlayListener {
        void onMp3PlayError(int state, String message);

        void onPlayStateChanged(int state, String message);
    }
}
