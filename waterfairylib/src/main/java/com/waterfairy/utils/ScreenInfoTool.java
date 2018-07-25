package com.waterfairy.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/12/15
 * @Description:
 */

public class ScreenInfoTool {
    private int screenHeight;
    private int screenWidth;
    private float density;
    private float densityDpi;

    private ScreenInfoTool() {
    }

    private final static ScreenInfoTool screenInfoTool = new ScreenInfoTool();

    public static ScreenInfoTool getInstance() {
        return screenInfoTool;
    }

    public void init(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            screenHeight = displayMetrics.heightPixels;
            screenWidth = displayMetrics.widthPixels;
            density = displayMetrics.density;
            densityDpi = displayMetrics.densityDpi;
        }
    }

    public int getScreenHeight(Context context) {
        if (screenHeight == 0) init(context);
        return screenHeight;
    }

    public float getDensityDpi() {
        return densityDpi;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenWidth(Context context) {
        if (screenWidth == 0) init(context);
        return screenWidth;
    }

    public int getHeight() {
        return Math.max(getScreenHeight(), getScreenWidth());
    }

    public int getWidth() {
        return Math.min(getScreenHeight(), getScreenWidth());
    }


    public float getDensity() {
        return density;
    }

    public float getDensity(Context context) {
        if (density == 0) init(context);
        return density;
    }

    @Override
    public String toString() {
        return
                "density:\t" + getDensity() + "\n" +
                        "densityDpi:\t" + getDensityDpi() + "\n" +
                        "times:\t" + (getWidth() / getDensity() / 360) + "\n" +
                        "width:\t" + getWidth() + "\twidthDP:\t" + (getWidth() / getDensity()) + "\n" +
                        "height:\t" + getHeight() + "\theightDP:\t" + (getHeight() / getDensity()) + "\n";
    }
}
