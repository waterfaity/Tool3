package com.waterfairy.socket.listener;

/**
 * Created by water_fairy on 2017/3/13.
 */

public interface OnSocketDeviceSearchListener {
    void onSearch(String ipAddress);

    void onRightSearch(String ipAddress);

    void onSearchFinish();
}
