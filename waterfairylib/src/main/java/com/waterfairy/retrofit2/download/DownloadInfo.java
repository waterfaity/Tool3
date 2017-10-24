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
}
