package com.waterfairy.utils;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/1
 * @Description:
 */

public class AngleUtils {

    private static final String TAG = "angleUtils";

    public static Float[] getLineK(float x, float y, float x1, float y1) {
        Float gradient = null;
        float dx = x1 - x;
        if (dx == 0) {
            Float sign = 1f;
            if (y1 < y) {
                sign = -1f;
            } else if (y1 == y) {
                gradient = 0f;
            }
            return new Float[]{gradient, sign, 0f};
        } else {
            float dy = y1 - y;
            gradient = (y1 - y) / dx;
            Float value = null;
            Float sign = 0f;
            if (dx > 0) {
                if (dy > 0) {
                    value = 0f;
                } else {
                    value = 360f;
                }
            } else {
                value = 180f;
            }
            return new Float[]{gradient, sign, value};
        }
    }

    /**
     * 由斜率获取角度
     * @param k
     * @return
     */
    public static double getAngle(Float k) {
        double radians = (float) Math.atan(k);
        return Math.toDegrees(radians);
    }

    public static double getAngle(Float[] k) {
        if (k[0] == null) {
            if (k[1] == 1) return 90;
            else return 270;
        } else {
            return getAngle(k[0]) + k[2];
        }
    }

    public static double getAngle(Float[] k, Float[] k1) {
        return getAngle(k1) - getAngle(k);
    }



}
