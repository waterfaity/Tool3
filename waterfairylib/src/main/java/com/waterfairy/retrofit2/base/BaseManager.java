package com.waterfairy.retrofit2.base;

import com.waterfairy.retrofit2.download.DownloadControl;
import com.waterfairy.retrofit2.upload.UploadControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by shui on 2017/5/6.
 */

public abstract class BaseManager extends IBaseManager {

    //    针对每个control
    public static final int START = 1;//开始
    public static final int PAUSE = 2;//暂停
    public static final int STOP = 3;//停止
    public static final int CONTINUE = 4;//继续下载中
    public static final int FINISHED = 5;//完成

    public static final int ERROR_NET = 6;//网络错误
    public static final int ERROR_FILE_CREATE = 7;//文件创建失败
    public static final int ERROR_FILE_SAVE = 8;//文件保存错误
    public static final int ERROR_IS_DOWNLOADING = 9;//文件已在下载中
    public static final int ERROR_HAS_FINISHED = 10;//文件已经下载完成
    public static final int ERROR_HAS_STOP = 11;//文件下载已停止
    public static final int ERROR_FILE_NOT_FOUND = 12;//文件不存在
    //    针对所有
    public static final int ERROR_NO_DOWNLOAD = 100;//没有下载任务
    public static final int REMOVE = 101;//移除
    public static final int REMOVE_ALL = 102;//全部移除
    public static final int START_ALL = 103;//开始全部
    public static final int PAUSE_ALL = 104;//暂停全部
    public static final int STOP_ALL = 105;//停止全部

    public HashMap<String, IBaseControl> controlHashMap;
    private OnAllHandleListener onAllHandleListener;


    /**
     * 添加下载
     *
     * @param downloadInfo
     * @return
     */
    @Override
    public IBaseControl add(BaseProgressInfo downloadInfo) {
        if (downloadInfo == null) return null;
        IBaseControl baseControl = get(downloadInfo.getUrl());
        if (baseControl == null) {
            baseControl = newDownloadControl(downloadInfo);
            if (baseControl == null) baseControl = newUploadControl(downloadInfo);
            controlHashMap.put(downloadInfo.getUrl(), baseControl);
        }
        return baseControl;
    }


    /**
     * 获取下载
     *
     * @param url
     * @return
     */
    @Override
    public IBaseControl get(String url) {
        if (controlHashMap == null) controlHashMap = new HashMap<>();
        return controlHashMap.get(url);
    }


    /**
     * 移除下载
     *
     * @param url
     */
    @Override
    public boolean remove(String url) {
        if (stop(url)) {
            controlHashMap.remove(url);
            if (onAllHandleListener != null) onAllHandleListener.onAllHandle(REMOVE);
            return true;
        }
        return false;
    }

    public boolean stop(String url) {
        if (controlHashMap != null) {
            IBaseControl iDownloadControl = controlHashMap.get(url);
            if (iDownloadControl != null) {
                iDownloadControl.stop();
                return true;
            }
        }
        return false;
    }

    /**
     * 移除所有下载
     */
    @Override
    public boolean removeAll() {
        if (controlHashMap != null) {
            if (controlHashMap.size() == 0) {
                if (onAllHandleListener != null) onAllHandleListener.onAllHandle(ERROR_NO_DOWNLOAD);
                return false;
            }
            Set<String> strings = controlHashMap.keySet();
            List<String> keyList = new ArrayList<>();
            for (String string : strings) {
                if (stop(string)) {
                    keyList.add(string);
                }
            }
            for (int i = 0; i < keyList.size(); i++) {
                controlHashMap.remove(keyList.get(i));
            }
            if (onAllHandleListener != null) onAllHandleListener.onAllHandle(REMOVE_ALL);
        } else {
            return false;
        }
        return true;
    }

    /**
     * 暂停所有下载
     */
    @Override
    public boolean pauseAll() {
        return handle(PAUSE_ALL);
    }

    /**
     * 停止所有下载
     */
    @Override
    public boolean stopAll() {
        return handle(STOP_ALL);
    }

    /**
     * 开始所有下载
     */
    @Override
    public boolean startAll() {
        return handle(START_ALL);
    }

    /**
     * 下载完成
     *
     * @param url
     */
    @Override
    public void onFinished(String url) {
        controlHashMap.remove(url);
    }

    protected abstract DownloadControl newDownloadControl(BaseProgressInfo downloadInfo);

    protected abstract UploadControl newUploadControl(BaseProgressInfo downloadInfo);


    public interface OnAllHandleListener {
        void onAllHandle(int code);
    }

    public void setOnAllHandleListener(OnAllHandleListener onAllHandleListener) {
        this.onAllHandleListener = onAllHandleListener;
    }

    private boolean handle(int code) {
        if (controlHashMap != null) {
            if (controlHashMap.size() == 0) {
                if (onAllHandleListener != null) onAllHandleListener.onAllHandle(ERROR_NO_DOWNLOAD);
                return false;
            }
            Set<String> strings = controlHashMap.keySet();
            for (String string : strings) {
                IBaseControl iDownloadControl = controlHashMap.get(string);
                switch (code) {
                    case START_ALL:
                        iDownloadControl.start();
                        break;
                    case STOP_ALL:
                        iDownloadControl.stop();
                        break;
                    case PAUSE_ALL:
                        iDownloadControl.pause();
                        break;
                }
            }
            if (onAllHandleListener != null) onAllHandleListener.onAllHandle(code);
            return true;
        } else {
            return false;
        }
    }
}
