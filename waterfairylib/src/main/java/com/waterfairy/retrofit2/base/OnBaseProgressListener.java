package com.waterfairy.retrofit2.base;

/**
 * Created by shui on 2017/5/6.
 */

public interface OnBaseProgressListener {
    void onDownloading(boolean done, long total, long current);
}
