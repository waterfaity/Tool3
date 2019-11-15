package com.waterfairy.media.lame;

import android.os.AsyncTask;

import com.cloud.classroom.audiorecord.lame.SimpleLame;
import com.cloud.classroom.audiorecord.lame.SimpleLameCallBack;

import java.io.File;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/5/7 11:36
 * @info:
 */
public class CoverMp3Tool {


    public void trans(String wavPath, String mp3Path, final OnCoverListener onCoverListener) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... strings) {

                SimpleLame.convertmp3(strings[0], strings[1], new SimpleLameCallBack() {
                    @Override
                    public void onProgress(int progress) {
                        publishProgress(progress);
                    }

                    @Override
                    public void onError(String message) {
//                        publishProgress(null);
                    }
                });
                return strings[1];
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (values == null) onCoverListener.onCoverError();
                else onCoverListener.onCoverProgress(values[0]);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (new File(s).exists())
                    onCoverListener.onCoverSuccess(s);
                else onCoverListener.onCoverError();
            }
        }.execute(wavPath, mp3Path);

    }

    public interface OnCoverListener {
        void onCoverSuccess(String mp3Path);

        void onCoverProgress(int progress);

        void onCoverError();
    }


}
