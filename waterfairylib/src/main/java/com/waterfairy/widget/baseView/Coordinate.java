package com.waterfairy.widget.baseView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/8 13:48
 * @info:
 */
public class Coordinate {
    public float x;
    public float y;
    public int value;
    public String text;

    public Coordinate() {
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(int x, int y, int value, String text) {
        this.x = x;
        this.value = value;
        this.y = y;
        this.text = text;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
