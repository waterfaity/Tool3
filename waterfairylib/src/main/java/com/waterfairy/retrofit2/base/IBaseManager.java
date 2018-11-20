package com.waterfairy.retrofit2.base;

/**
 * Created by shui on 2017/5/6.
 */

public abstract class IBaseManager {
    /**
     * 添加下载
     *
     * @param downloadInfo
     * @param tag
     * @return
     */
    abstract IBaseControl add(BaseProgressInfo downloadInfo, String tag);

    /**
     * 获取下载
     *
     * @param url
     * @return
     */
    abstract IBaseControl get(String url);

    /**
     * 移除下载(会删除当前下载中的文件)
     *
     * @param url
     */
    abstract boolean remove(String url);

    /**
     * 移除所有下载(会删除当前下载中的文件)
     */
    abstract boolean removeAll();

    /**
     * 暂停所有下载
     */
    abstract boolean pauseAll();

    /**
     * 停止所有下载
     */
    abstract boolean stopAll();

    /**
     * 开始所有下载
     */
    abstract boolean startAll();

    /**
     * 下载完成
     *
     * @param url
     */
    abstract void onFinished(String url);
}
