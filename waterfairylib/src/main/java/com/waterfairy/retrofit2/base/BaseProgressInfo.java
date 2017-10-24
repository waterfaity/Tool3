package com.waterfairy.retrofit2.base;

/**
 * Created by water_fairy on 2017/6/26.
 * 995637517@qq.com
 */

public class BaseProgressInfo {

//    public static final int START = 1;
//    public static final int DOWNLOADING = 2;
//    public static final int PAUSE = 3;
//    public static final int STOP = 4;
//    public static final int FINISH = 5;
//    public static final int ERROR = 6;

    protected String url;//下载路径
    protected String basePath;//基础路径
    protected long currentLen;//当前下载的位置
    protected long lastLen;//上次下载的位置
    protected long totalLen;//总长度
    protected int timeOut = 5;//超时 s
    protected int state;//下载状态
    protected String extraInfo2;
    protected String extraInfo;

    public String getExtraInfo2() {
        return extraInfo2;
    }

    public void setExtraInfo2(String extraInfo2) {
        this.extraInfo2 = extraInfo2;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public long getCurrentLen() {
        return currentLen;
    }

    public void setCurrentLen(long currentLen) {
        this.currentLen = currentLen;
    }

    public long getLastLen() {
        return lastLen;
    }

    public void setLastLen(long lastLen) {
        this.lastLen = lastLen;
    }

    public long getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(long totalLen) {
        this.totalLen = totalLen;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
