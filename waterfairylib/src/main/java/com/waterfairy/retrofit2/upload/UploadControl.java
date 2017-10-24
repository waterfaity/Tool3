package com.waterfairy.retrofit2.upload;

import com.waterfairy.retrofit2.base.BaseManager;
import com.waterfairy.retrofit2.base.BaseProgress;
import com.waterfairy.retrofit2.base.BaseProgressInfo;
import com.waterfairy.retrofit2.base.IBaseControl;
import com.waterfairy.retrofit2.base.OnBaseProgressSuccessListener;
import com.waterfairy.retrofit2.base.OnProgressListener;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by water_fairy on 2017/6/26.
 * 995637517@qq.com
 */

public class UploadControl extends IBaseControl implements OnBaseProgressSuccessListener {
    private IUploadService uploadService;
    private BaseProgress baseProgress;

    public UploadControl(BaseProgressInfo baseProgressInfo) {
        this.baseProgressInfo = baseProgressInfo;
        this.url = baseProgressInfo.getUrl();
        initUpload();
    }


    public void initUpload() {
        if (uploadService == null) {
            baseProgress = new BaseProgress(baseProgressInfo, this);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(baseProgressInfo.getTimeOut(), TimeUnit.SECONDS)
                    .build();
            uploadService = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseProgressInfo.getBasePath())
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(Executors.newCachedThreadPool())
                    .build()
                    .create(IUploadService.class);
        }
    }

    public void start() {

        if (baseProgressState == BaseManager.CONTINUE) {
            returnError(BaseManager.CONTINUE);
        } else {
            UploadInfo uploadInfo = (UploadInfo) baseProgressInfo;
            File file = new File(uploadInfo.getFilePath());
            if (!file.exists()) {
                baseProgressState = BaseManager.ERROR_FILE_NOT_FOUND;
                returnError(BaseManager.ERROR_FILE_NOT_FOUND);
            } else {
                if (baseProgressState == BaseManager.START || baseProgressState == BaseManager.CONTINUE) {
                    returnError(BaseManager.ERROR_IS_DOWNLOADING);
                } else if (baseProgressState == BaseManager.FINISHED) {
                    returnError(BaseManager.ERROR_HAS_FINISHED);
                } else {
                    returnChange(BaseManager.CONTINUE);
                    RequestBody sourceBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
                    UploadRequestBody uploadRequestBody = new UploadRequestBody(sourceBody, baseProgress);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                            "file",
                            ((UploadInfo) baseProgressInfo).getFileName(),
                            uploadRequestBody);
                    uploadService.upload(baseProgressInfo.getUrl(), filePart).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            baseProgressInfo.setState(BaseManager.FINISHED);
                            returnChange(BaseManager.FINISHED);
                            OnProgressListener loadListener = getLoadListener();
                            if (loadListener != null) {
                                loadListener.onResponse(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            baseProgressState = BaseManager.ERROR_FILE_NOT_FOUND;
                            returnError(BaseManager.ERROR_NET);
                            OnProgressListener loadListener = getLoadListener();
                            if (loadListener != null) {
                                loadListener.onError(t);
                            }
                        }
                    });
                }

            }

        }

    }

    @Override
    public void pause() {
        if (call != null)
            call.cancel();
        returnChange(BaseManager.PAUSE);
    }


    @Override
    public void stop() {

    }

    @Override
    public OnProgressListener getLoadListener() {
        OnProgressListener onProgressListener = null;
        if (baseProgress != null)
            onProgressListener = baseProgress.getOnProgressListener();
        return onProgressListener;
    }

    @Override
    public UploadControl setLoadListener(OnProgressListener onProgressListener) {
        if (baseProgress != null)
            baseProgress.setOnProgressListener(onProgressListener);
        return this;
    }

    @Override
    public void onProgressSuccess(String url) {

    }
}
