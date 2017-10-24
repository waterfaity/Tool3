package com.waterfairy.retrofit2.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by shui on 2017/4/26.
 */

public interface IDownloadService  {
    @Streaming
    @GET
    Call<ResponseBody> download(@Header("RANGE") String start, @Url String url);
}
