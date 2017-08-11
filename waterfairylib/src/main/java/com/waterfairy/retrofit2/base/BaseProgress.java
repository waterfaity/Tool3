package com.waterfairy.retrofit2.base;

/**
 * Created by shui on 2017/5/6.
 */

public class BaseProgress implements OnBaseProgressListener {
    private OnProgressListener onProgressListener;//开放给用户的接口
    private OnBaseProgressSuccessListener onBaseProgressSuccessListener;//内部接口 下载完成时关闭control
    private BaseProgressInfo downloadInfo;//下载/上传信息

    public void setOnProgressListener(OnProgressListener onBaseListener) {
        this.onProgressListener = onBaseListener;

    }

    public OnProgressListener getOnProgressListener() {
        return onProgressListener;

    }

    public BaseProgress(BaseProgressInfo downloadInfo, OnBaseProgressSuccessListener onBaseProgressSuccessListener) {
        this.downloadInfo = downloadInfo;
        this.onBaseProgressSuccessListener = onBaseProgressSuccessListener;
    }


    @Override
    public void onDownloading(boolean done, long total, long current) {
        current = downloadInfo.getLastLen() + current;
        downloadInfo.setCurrentLen(current);
        if (done) {
            downloadInfo.setState(BaseProgressInfo.FINISH);
            onBaseProgressSuccessListener.onProgressSuccess(downloadInfo.getUrl());
        }
        if (onProgressListener != null)
            onProgressListener.onLoading(done, downloadInfo.getTotalLen(), current);
    }

    public void setTotalLen(long totalLen) {
        downloadInfo.setTotalLen(totalLen);
    }

}