package com.waterfairy.retrofit2.download;

import com.waterfairy.retrofit2.base.OnBaseProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by shui on 2017/4/26.
 */

public class DownloadInterceptor implements Interceptor {
    private OnBaseProgressListener downloadingListener;

    public DownloadInterceptor(OnBaseProgressListener downloadingListener) {
        this.downloadingListener = downloadingListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response oriResponse = chain.proceed(chain.request());
        return oriResponse.newBuilder()
                .body(new DownloadResponseBody(oriResponse.body(), downloadingListener))
                .build();
    }
}
