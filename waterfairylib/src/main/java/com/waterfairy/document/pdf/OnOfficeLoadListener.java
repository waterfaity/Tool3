package com.waterfairy.document.pdf;

/**
 * Created by water_fairy on 2017/7/13.
 * 995637517@qq.com
 */

public interface OnOfficeLoadListener {
    void onLoading(String content);

    void onLoadSuccess();

    void onLoadError();
}
