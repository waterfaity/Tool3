package com.waterfairy.retrofit2.download;

import android.util.Log;

import com.waterfairy.retrofit2.base.BaseManager;
import com.waterfairy.retrofit2.base.BaseProgress;
import com.waterfairy.retrofit2.base.BaseProgressInfo;
import com.waterfairy.retrofit2.base.FileWriter;
import com.waterfairy.retrofit2.base.IBaseControl;
import com.waterfairy.retrofit2.base.OnBaseProgressSuccessListener;
import com.waterfairy.retrofit2.base.OnProgressListener;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shui on 2017/5/6.
 */

public class DownloadControl extends IBaseControl implements OnBaseProgressSuccessListener {


    private IDownloadService downloadService;

    public DownloadControl(BaseProgressInfo info) {
        this.baseProgressInfo = info;
        url = baseProgressInfo.getUrl();
        initDownload();
    }


    private void initDownload() {
        if (downloadService == null) {
            baseProgress = new BaseProgress(baseProgressInfo, this);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(baseProgressInfo.getTimeOut(), TimeUnit.SECONDS)
                    .addInterceptor(new DownloadInterceptor(baseProgress))
                    .build();
            downloadService = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseProgressInfo.getBasePath())
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(Executors.newCachedThreadPool())
                    .build()
                    .create(IDownloadService.class);
        }
    }


    public void start() {
        if (baseProgressState == BaseManager.CONTINUE || baseProgressState == BaseManager.START) {
            returnWarm(BaseManager.WARM_IS_DOWNLOADING);
        } else if (baseProgressState == BaseManager.FINISHED) {
            returnWarm(BaseManager.WARM_HAS_FINISHED);
//        }
//        else  if (baseProgressState == BaseManager.STOP) {
//            returnError(BaseManager.WARM_HAS_STOP);
        } else {
            returnChange(BaseManager.START);
            call = downloadService.download("bytes=" + baseProgressInfo.getCurrentLen() + "-", url);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    Log.i("test", "onResponse: 1");
                    returnChange(BaseManager.CONTINUE);
                    new FileWriter().writeFile(
                            DownloadControl.this,
                            getLoadListener(),
                            response.body(),
                            (DownloadInfo) baseProgressInfo);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.i("test", "onFailure: 1");
                    throwable.printStackTrace();
                    returnError(BaseManager.ERROR_NET);
                    baseProgressState = BaseManager.ERROR_NET;
                }
            });
        }
    }

    @Override
    public void pause() {
        if (BaseManager.PAUSE == baseProgressState) return;
        if (call != null)
            call.cancel();
        returnChange(BaseManager.PAUSE);
    }


    @Override
    public void stop() {
        if (baseProgressInfo != null) {
            baseProgressInfo.setCurrentLen(0);
            File file = new File(((DownloadInfo) baseProgressInfo).getSavePath());
            if (file.exists()) file.delete();
        }
        returnChange(BaseManager.STOP);
    }

    @Override
    public OnProgressListener getLoadListener() {
        OnProgressListener onProgressListener = null;
        if (baseProgress != null)
            onProgressListener = baseProgress.getOnProgressListener();
        return onProgressListener;
    }

    @Override
    public DownloadControl setLoadListener(OnProgressListener onProgressListener) {
        if (baseProgress != null)
            baseProgress.setOnProgressListener(onProgressListener);
        return this;
    }


    @Override
    public void onProgressSuccess(String url) {
        DownloadManager.getInstance().onFinished(url);
        returnChange(BaseManager.FINISHED);
    }
}
