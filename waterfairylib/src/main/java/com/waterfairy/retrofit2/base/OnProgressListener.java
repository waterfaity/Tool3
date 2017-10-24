package com.waterfairy.retrofit2.base;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by shui on 2017/4/26.
 */

public abstract class OnProgressListener {
    public void onResponse(Response<ResponseBody> response) {
    }


    public void onError(Throwable throwable) {
    }


    public abstract void onLoading(boolean done, long total, long current);

    public abstract void onError(int code);

    public abstract void onChange(int code);
}
