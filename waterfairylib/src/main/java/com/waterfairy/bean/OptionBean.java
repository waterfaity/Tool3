package com.waterfairy.bean;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/28 17:31
 * @info:
 */
public class OptionBean implements BaseBean {
    private String text;

    public OptionBean(int imgRes, boolean isRight) {
        this.imgRes = imgRes;
        this.isRight = isRight;
    }

    public OptionBean(String text, int imgRes, boolean isRight) {
        this.text = text;
        this.imgRes = imgRes;
        this.isRight = isRight;
    }

    public int imgRes;
    public boolean isRight;

    public int getImgRes() {
        return imgRes;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }
}