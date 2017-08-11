package com.waterfairy.retrofit2.base;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by shui on 2017/4/26.
 * 文件下载控制 返回boolean  值为isDownloading
 */

public abstract class IBaseControl {
    protected BaseProgressInfo baseProgressInfo;
    protected BaseProgress baseProgress;
    protected int baseProgressState;
    protected Call<ResponseBody> call;
    protected String url;


    /**
     * 暂停
     *
     * @return isDownloading
     */
    public abstract void pause();

    /**
     * 停止 同暂停(待定)
     *
     * @return isDownloading
     */
    public abstract void stop();

    public abstract void start();

    public int getState() {
        return baseProgressState;
    }

    protected void returnChange(int code) {
        baseProgressState = code;
        OnProgressListener downloadListener = getLoadListener();
        if (downloadListener != null) downloadListener.onChange(code);
    }

    protected void returnError(int code) {
        OnProgressListener downloadListener = getLoadListener();
        if (downloadListener != null) downloadListener.onError(code);
    }

    public abstract OnProgressListener getLoadListener();

    public BaseProgressInfo getBaseProgressInfo() {
        return baseProgressInfo;
    }

    protected abstract IBaseControl setLoadListener(OnProgressListener onProgressListener);

}
