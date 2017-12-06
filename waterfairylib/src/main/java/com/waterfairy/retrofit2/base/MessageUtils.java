package com.waterfairy.retrofit2.base;

/**
 * Created by water_fairy on 2017/7/14.
 * 995637517@qq.com
 */

public class MessageUtils {


    public static String getMsg(int code) {
        return getMsg(true, code);
    }

    public static String getMsg(boolean isDown, int code) {
        String msg = "";
        switch (code) {
            case BaseManager.START:
                msg = "开始下载";
                break;
            case BaseManager.PAUSE:
                msg = "暂停下载";
                break;
            case BaseManager.STOP:
                msg = "停止下载";
                break;
            case BaseManager.CONTINUE:
                msg = "下载中";
                break;
            case BaseManager.FINISHED:
                msg = "完成下载";
                break;
            case BaseManager.ERROR_NET:
                msg = "网络错误";
                break;
            case BaseManager.ERROR_FILE_CREATE:
                msg = "文件创建失败";
                break;
            case BaseManager.ERROR_FILE_SAVE:
                msg = "文件保存错误";
                break;
            case BaseManager.WARM_IS_DOWNLOADING:
                msg = "文件已在下载中";
                break;
            case BaseManager.WARM_HAS_FINISHED:
                msg = "文件已经下载完成";
                break;
            case BaseManager.WARM_HAS_STOP:
                msg = "文件下载已停止";
                break;
            case BaseManager.ERROR_FILE_NOT_FOUND:
                msg = "文件不存在";
                break;
            case BaseManager.ERROR_NO_DOWNLOAD:
                msg = "没有下载任务";
                break;
            case BaseManager.REMOVE:
                msg = "移除下载";
                break;
            case BaseManager.REMOVE_ALL:
                msg = "移除全部下载";
                break;
            case BaseManager.START_ALL:
                msg = "开始全部下载";
                break;
            case BaseManager.PAUSE_ALL:
                msg = "暂停全部下载";
                break;
            case BaseManager.STOP_ALL:
                msg = "停止全部下载";
                break;
            case BaseManager.ERROR_STOP:
                msg = "下载被终止";
                break;
        }
        if (!isDown) {
            msg = msg.replace("下载", "上传");
        }
        return msg;
    }
}
