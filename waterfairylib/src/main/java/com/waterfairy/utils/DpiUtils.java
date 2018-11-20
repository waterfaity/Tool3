package com.waterfairy.utils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/31 18:00
 * @info:
 */
public class DpiUtils {
    public static final float DPI_XXX_H = 4F;//4
    public static final float DPI_XX_H = 3F;//3
    public static final float DPI_X_H = 2F;//2
    public static final float DPI_H = 1.5F;//1.5
    public static final float DPI_M = 1F;//1
    public static final float DPI_L = 0.75F;//0.75

    public static float getMValue(float type, float value) {
        if (type <= 0) return 0;
        return value / type;
    }

    public static float getValue(float srcType, float targetType, float value) {
        if (srcType <= 0 || targetType <= 0) return 0;
        return value / srcType * targetType;
    }
}
