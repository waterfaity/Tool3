package com.waterfairy.widget;

/**
 * Created by water_fairy on 2017/5/25.
 * 995637517@qq.com
 */

public class HistogramEntity {
    public HistogramEntity(int value, String xName) {
        this.value = value;
        this.xName = xName;
    }

    private int value;
    private String xName;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getxName() {
        return xName;
    }

    public void setxName(String xName) {
        this.xName = xName;
    }
}
