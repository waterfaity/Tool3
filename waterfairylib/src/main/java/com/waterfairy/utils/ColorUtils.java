package com.waterfairy.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by water_fairy on 2017/6/5.
 * 995637517@qq.com
 */

public class ColorUtils {
    public static int randomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    //    Y(亮度)=(0.299*R)+(0.587*G)+(0.114*B)
    public int getColor(Bitmap sourceBitmap, int x, int y) {
        if (sourceBitmap == null) return Color.WHITE;
        else return sourceBitmap.getPixel(x, y);
    }

    public String getColorHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));

    }

    /**
     * 修改颜色透明度
     *
     * @param color
     * @param alpha
     * @return
     */
    public static int changeAlpha(int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int[] getColorSystem(
            int startColorRed, int endColorRed,
            int startColorGreen, int endColorGreen,
            int startColorBlue, int endColorBlue,
            int num) {

        if (num == 0) {
//            return new int[]{  Color.rgb(startColorRed, startColorGreen, startColorBlue)};
            return new int[]{};
        }
        int dbRed = endColorRed - startColorRed;
        int dbGreen = endColorGreen - startColorGreen;
        int dbBlue = endColorBlue - startColorBlue;

        int perRed = dbRed / num;
        int perGreen = dbGreen / num;
        int perBlue = dbBlue / num;

        int[] colors = new int[num];
        for (int i = 0; i < num; i++) {
            colors[i] = Color.rgb(
                    startColorRed + i * perRed,
                    startColorGreen + i * perGreen,
                    startColorBlue + i * perBlue
            );
        }
        return colors;

    }
}
