package com.waterfairy.widget.baseView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/15 09:27
 * @info:
 */
public class FloatEntity {
    public float value;
    public String valueStr;
    public String name;

    public FloatEntity() {
    }

    public FloatEntity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }
}
