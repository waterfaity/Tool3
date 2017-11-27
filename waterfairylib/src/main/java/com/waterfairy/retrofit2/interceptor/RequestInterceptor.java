package com.waterfairy.retrofit2.interceptor;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by water_fairy on 2017/5/18.
 * 995637517@qq.com
 */

public abstract class RequestInterceptor implements Interceptor {
    HashMap<String, String> params;
    protected boolean showUrl;

    public RequestInterceptor(HashMap<String, String> hashMap) {
        this.params = hashMap;
    }

    public RequestInterceptor() {

    }

    public RequestInterceptor(boolean showUrl) {
        this.showUrl = showUrl;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (params != null && params.size() > 0) {
            Request newRequest = null;
            RequestBody body = request.body();
            HttpUrl url = request.url();
            if (body != null) {
                if (body instanceof FormBody) {
                    //单类型
                    body = addParamsToFormBody((FormBody) body);
                } else if (body instanceof MultipartBody) {
                    //多类型
                    body = addParamsToMulFormBody((MultipartBody) body);
                }
            } else {
                url = addParamsToUrl(url);
            }
            if (showUrl) {
                showUrl(url, body);
            }
            newRequest = request.newBuilder()
                    .url(url)
                    .method(request.method(), body)
                    .build();
            return chain.proceed(newRequest);
        }
        return chain.proceed(request);
    }

    private void showUrl(HttpUrl url, RequestBody body) {
        String urlTemp = url.url().toString();
        if (body instanceof FormBody) {
            FormBody formBody = (FormBody) body;
            int size = formBody.size();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) buffer.append('&');
                buffer.append(formBody.name(i));
                buffer.append('=');
                buffer.append(formBody.value(i));
            }
            urlTemp += ("?" + buffer.toString());
        }
        Log.i("RequestInterceptor", "url: " + urlTemp);
    }

    /**
     * get请求添加数据
     *
     * @param url
     * @return
     */
    private HttpUrl addParamsToUrl(HttpUrl url) {
        String oriUrl = url.toString();
        StringBuilder stringBuilder = new StringBuilder(oriUrl);
        HashMap<String, String> tempParams = new HashMap<>();
        Set<String> strings = params.keySet();
        for (String key : strings) {
            String value = params.get(key);
            stringBuilder.append("&").append(key).append("=").append(value);
            tempParams.put(key, value);
        }
        Set<String> strings2 = url.queryParameterNames();
        for (String key : strings2) {
            String value = url.queryParameter(key);
            tempParams.put(key, value);
        }
        putExtraParamsToUrl(stringBuilder, tempParams);
        return HttpUrl.parse(stringBuilder.toString());
    }

    /**
     * 多类型 添加数据
     *
     * @param multipartBody
     * @return
     */
    private RequestBody addParamsToMulFormBody(MultipartBody multipartBody) {
        HashMap<String, String> tempParams = new HashMap<>();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Set<String> strings = params.keySet();
        for (String key : strings) {
            String value = params.get(key);
            builder.addFormDataPart(key, value);
            tempParams.put(key, value);
        }
        for (int i = 0; i < multipartBody.size(); i++) {
            builder.addPart(multipartBody.part(i));
        }
        putExtraParamsToMulFormBody(builder, tempParams);
        return builder.build();
    }


    /**
     * 单form 添加数据
     *
     * @param formBody
     * @return
     */
    private FormBody addParamsToFormBody(FormBody formBody) {
        HashMap<String, String> tempParams = new HashMap<>();
        FormBody.Builder builder = new FormBody.Builder();
        Set<String> strings = params.keySet();
        for (String key : strings) {
            String value = params.get(key);
            if (!TextUtils.isEmpty(value)) {
                builder.add(key, value);
                tempParams.put(key, value);
            }
        }
        for (int i = 0; i < formBody.size(); i++) {
            String value = formBody.value(i);
            if (!TextUtils.isEmpty(value)) {
                String key = formBody.name(i);
                builder.add(key, value);
                tempParams.put(key, value);
            }
        }
        putExtraParamsToFromBody(builder, tempParams);
        return builder.build();
    }

    public void setPopParams(HashMap<String, String> params) {
        this.params = params;
    }

    abstract void putExtraParamsToUrl(StringBuilder stringBuilder, HashMap<String, String> tempParams);

    abstract void putExtraParamsToFromBody(FormBody.Builder builder, HashMap<String, String> tempParams);

    abstract void putExtraParamsToMulFormBody(MultipartBody.Builder builder, HashMap<String, String> tempParams);

}
