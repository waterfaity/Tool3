/* 
 * Copyright (c) 2011-2012 Yuichi Hirano
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cloud.classroom.audiorecord.lame;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * マイクから取得した音声をMP3に保存する
 * <p>
 * 別スレッドでマイクからの録音、MP3への変換を行う
 */
public class RecMicToMp3 {

    static {
        System.loadLibrary("mp3lame");
    }

    /**
     * MP3ファイルを保存するファイルパス
     */
    private String mFilePath;

    /**
     * サンプリングレート
     */
    private int mSampleRate;

    /**
     * 録音中か
     */
    private boolean mIsRecording = false;

    /**
     * 録音の状態変化を通知するハンドラ
     *
     * @see RecMicToMp3#MSG_REC_STARTED
     * @see RecMicToMp3#MSG_REC_STOPPED
     * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
     * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
     * @see RecMicToMp3#MSG_ERROR_REC_START
     * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
     * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
     * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
     * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
     */
    private Handler mHandler;

    /**
     * 录音开始
     */
    public static final int MSG_REC_STARTED = 0;

    /**
     * 录音停止
     */
    public static final int MSG_REC_STOPPED = 1;

    /**
     * 不能得到缓冲区的大小。这是可能的，该终端不支持设置采样率等。
     */
    public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = 2;

    /**
     * 无法生成文件
     */
    public static final int MSG_ERROR_CREATE_FILE = 3;

    /**
     * 录音开始失败了
     */
    public static final int MSG_ERROR_REC_START = 4;

    /**
     * 录音不能。录音里开始后才发行的。
     */
    public static final int MSG_ERROR_AUDIO_RECORD = 5;

    /**
     * 编码失败了。录音里开始后才发行的。
     */
    public static final int MSG_ERROR_AUDIO_ENCODE = 6;

    /**
     * 文件的开头失败了。录音里开始后才发行的。
     */
    public static final int MSG_ERROR_WRITE_FILE = 7;

    /**
     * 文件关闭失败了。录音里开始后才发行的。
     */
    public static final int MSG_ERROR_CLOSE_FILE = 8;

    /**
     * コンストラクタ
     *
     * @param filePath   保存するファイルパス
     * @param sampleRate 録音するサンプリングレート（Hz）
     */
    public RecMicToMp3(String filePath, int sampleRate) {
        if (sampleRate <= 0) {
            throw new InvalidParameterException(
                    "Invalid sample rate specified.");
        }
        this.mFilePath = filePath;
        this.mSampleRate = sampleRate;
    }

    /**
     * 録音を開始する
     */
    public void start() {
        // 録音中の場合は何もしない
        if (mIsRecording) {
            return;
        }

        // 録音を別スレッドで開始する
        new Thread() {
            @Override
            public void run() {
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                // 最低限のバッファサイズ
                final int minBufferSize = AudioRecord.getMinBufferSize(
                        mSampleRate, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                // バッファサイズが取得できない。サンプリングレート等の設定を端末がサポートしていない可能性がある。
                if (minBufferSize < 0) {
//                    if (mHandler != null) {
//                        mHandler.sendEmptyMessage(MSG_ERROR_GET_MIN_BUFFERSIZE);
//                    }
                    sendMessage(MSG_ERROR_GET_MIN_BUFFERSIZE, "录音失败,该终端不支持设置采样率");
                    return;
                }
                // getMinBufferSizeで取得した値の場合
                // "W/AudioFlinger(75): RecordThread: buffer overflow"が発生するようであるため、少し大きめの値にしている
                AudioRecord audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, mSampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

                // PCM buffer size (5sec)
                short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5]; // SampleRate[Hz]
                // *
                // 16bit
                // *
                // Mono
                // *
                // 5sec
                byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(new File(mFilePath));
                } catch (FileNotFoundException e) {
                    // ファイルが生成できない
//                    if (mHandler != null) {
//                        mHandler.sendEmptyMessage(MSG_ERROR_CREATE_FILE);
//                    }
                    sendMessage(MSG_ERROR_CREATE_FILE, "录音失败,无法生成音频文件");
                    return;
                }

                // Lame init
                SimpleLame.init(mSampleRate, 1, mSampleRate, 32);

                mIsRecording = true; // 録音の開始フラグを立てる
                try {
                    try {
                        audioRecord.startRecording(); // 録音を開始する
                    } catch (IllegalStateException e) {
                        // 録音の開始に失敗した
//                        if (mHandler != null) {
//                            mHandler.sendEmptyMessage(MSG_ERROR_REC_START);
//                        }
                        sendMessage(MSG_ERROR_REC_START, "录音开始失败");
                        return;
                    }

                    try {
                        // 録音が開始した
//                        if (mHandler != null) {
//                            mHandler.sendEmptyMessage(MSG_REC_STARTED);
//                        }
                        sendMessage(MSG_REC_STARTED, "录音开始");
                        int readSize = 0;
                        while (mIsRecording) {
                            readSize = audioRecord.read(buffer, 0,
                                    minBufferSize);
                            if (readSize > 0) {
                                decibel(buffer, readSize, buffer.length);
                            }
                            if (readSize < 0) {
                                // 録音ができない
//                                if (mHandler != null) {
//                                    mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
//                                }
                                sendMessage(MSG_ERROR_AUDIO_RECORD, "录音失败");
                                break;
                            }
                            // データが読み込めなかった場合は何もしない
                            else if (readSize == 0) {
                                ;
                            }
                            // データが入っている場合
                            else {
                                int encResult = SimpleLame.encode(buffer,
                                        buffer, readSize, mp3buffer);
                                if (encResult < 0) {
                                    // エンコードに失敗した
//                                    if (mHandler != null) {
//                                        mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
//                                    }
                                    sendMessage(MSG_ERROR_AUDIO_ENCODE, "录音编码失败");
                                    break;
                                }
                                if (encResult != 0) {
                                    try {
                                        output.write(mp3buffer, 0, encResult);
                                    } catch (IOException e) {
                                        // ファイルの書き出しに失敗した
//                                        if (mHandler != null) {
//                                            mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
//                                        }
                                        sendMessage(MSG_ERROR_WRITE_FILE, "无法生成音频文件");
                                        break;
                                    }
                                }
                            }
                        }

                        int flushResult = SimpleLame.flush(mp3buffer);
                        if (flushResult < 0) {
                            // エンコードに失敗した
//                            if (mHandler != null) {
//                                mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
//                            }
                            sendMessage(MSG_ERROR_AUDIO_ENCODE, "录音编码失败");
                        }
                        if (flushResult != 0) {
                            try {
                                output.write(mp3buffer, 0, flushResult);
                            } catch (IOException e) {
                                // ファイルの書き出しに失敗した
//                                if (mHandler != null) {
//                                    mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
//                                }
                                sendMessage(MSG_ERROR_WRITE_FILE, "无法生成音频文件");
                            }
                        }

                        try {
                            output.close();
                        } catch (IOException e) {
                            // ファイルのクローズに失敗した
//                            if (mHandler != null) {
//                                mHandler.sendEmptyMessage(MSG_ERROR_CLOSE_FILE);
//                            }
                            sendMessage(MSG_ERROR_CLOSE_FILE, "文件保存失败");
                        }
                    } finally {
                        audioRecord.stop(); // 録音を停止する
                        audioRecord.release();
                    }
                } finally {
                    SimpleLame.close();
                    mIsRecording = false; // 録音の開始フラグを下げる
                }

                // 録音が終了した
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_REC_STOPPED);
                }
                sendMessage(MSG_REC_STOPPED, "录音停止");
            }
        }.start();
    }

    public void decibel(short[] buffer, int size, int bufferMaxLength) {
        long v = 0;
        // 将 buffer 内容取出，进行平方和运算
        for (int i = 0; i < size; i++) {
            v += buffer[i] * buffer[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = v / (double) bufferMaxLength;
        double volume = 10 * Math.log10(mean);
        if (mHandler != null) {
            Message message = new Message();
            message.what = -1;
            message.obj = volume;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 録音を停止する
     */
    public void stop() {
        mIsRecording = false;
    }

    /**
     * 録音中かを取得する
     *
     * @return trueの場合は録音中、それ以外はfalse
     */
    public boolean isRecording() {
        return mIsRecording;
    }

    /**
     * 録音の状態変化を通知するハンドラを設定する
     *
     * @param handler 録音の状態変化を通知するハンドラ
     * @see RecMicToMp3#MSG_REC_STARTED
     * @see RecMicToMp3#MSG_REC_STOPPED
     * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
     * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
     * @see RecMicToMp3#MSG_ERROR_REC_START
     * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
     * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
     * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
     * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
     */
    public void setHandle(Handler handler) {
        this.mHandler = handler;
    }

    private void sendMessage(int what, String msg) {
        if (mHandler != null) {
            Message message = Message.obtain();
            message.what = what;
            message.obj = msg;
            mHandler.sendMessage(message);
        }
    }
}
