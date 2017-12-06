package com.waterfairy.retrofit2.download;

import com.waterfairy.retrofit2.base.BaseProgressInfo;

/**
 * Created by shui on 2017/4/26.
 */

public class DownloadInfo extends BaseProgressInfo {
    public DownloadInfo() {

    }

    public DownloadInfo(String basePath, String savePath, String url) {
        this.basePath = basePath;
        this.savePath = savePath;
        this.url = url;
    }

    protected String savePath;//保存路径

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    //
//    public abstract String getUrl();
//
//    public abstract void setUrl(String url);
//
//    public abstract String getBasePath();
//
//    public abstract void setBasePath(String basePath);
//
//    public abstract String getSavePath();
//
//    public abstract void setSavePath(String savePath);
//
//    public abstract long getCurrentLen();
//
//    public abstract void setCurrentLen(long currentLen);
//
//    public abstract long getLastLen();
//
//    public abstract void setLastLen(long lastLen);
//
//    public abstract long getTotalLen();
//
//    public abstract void setTotalLen(long totalLen);
//
//    public abstract int getTimeOut();
//
//    public abstract void setTimeOut(int timeOut);
//
//    public abstract int getState();
//
//    public abstract void setState(int state);
}
