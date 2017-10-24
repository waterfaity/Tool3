package com.waterfairy.retrofit2.upload;

import com.waterfairy.retrofit2.base.BaseProgressInfo;

import java.util.HashMap;

/**
 * Created by water_fairy on 2017/6/26.
 * 995637517@qq.com
 */

public class UploadInfo extends BaseProgressInfo {
    private HashMap<String, String> hashMap;
    private String filePath;
    private String fileName;


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public UploadInfo(String basePath, String url, String filePath, String fileName) {
        this.basePath = basePath;
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    public String getFilePath() {
        return filePath;
    }
}
