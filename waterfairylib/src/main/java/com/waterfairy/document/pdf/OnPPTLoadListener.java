package com.waterfairy.document.pdf;

/**
 * Created by water_fairy on 2017/8/25.
 * 995637517@qq.com
 */

public interface OnPPTLoadListener {
    void onLoadSuccess();

    void onLoadError();

    void onLoading(String content);
}
