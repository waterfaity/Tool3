package com.waterfairy.utils;

import java.io.IOException;

/**
 * Created by water_fairy on 2017/7/28.
 * 995637517@qq.com
 */

public class PingUtils {
    /**
     * @param ip IP地址 或网址
     * @return 0  成功; 1 不成功; -1 异常; 2 ip错误
     */
    public static int ping(String ip) {
        String ping = "ping -c 1 -w 0.5 ";//其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
        Runtime runtime = Runtime.getRuntime();
        Process exec = null;
        try {
            exec = runtime.exec(ping + ip);
            return exec.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
