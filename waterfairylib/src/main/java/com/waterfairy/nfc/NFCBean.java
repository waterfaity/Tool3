package com.waterfairy.nfc;

/**
 * Created by water_fairy on 2017/7/12.
 * 995637517@qq.com
 */

public class NFCBean {

    /**
     * 传统
     * MifareClassic卡片类型   不支持NDEF消息  \n
     */
    public static final int TYPE_CLASSICAL = -2;
    /**
     * 未知
     */
    public static final int TYPE_UNKNOWN = -1;
    /**
     * A MIFARE Ultralight tag
     */
    public static final int TYPE_ULTRALIGHT = 1;
    /**
     * A MIFARE Ultralight C tag
     */
    public static final int TYPE_ULTRALIGHT_C = 2;
    private int type;//类型
    private int maxSize;
    private String id;
    private String message;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(byte[] bytes) {
        this.message = new String(bytes);
    }

    public String getMessage() {
        return message;
    }

    public void addMessage(String message) {
        this.message += message;
    }
}
