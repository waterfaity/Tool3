package com.waterfairy.widget.utils;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 11:04
 * @info:
 */
public class PathUtils {
    /**
     * 指定 rect 内获取圆角
     *
     * @param rect
     * @param radius
     * @return
     */
    public static Path getCorner(RectF rect, int radius) {
        Path path = new Path();
        //计算path
        if (radius <= 0) {
            path.addRect(rect, Path.Direction.CW);
        } else {
            float radius2 = 2 * radius;
            path.moveTo(rect.left + radius, rect.top);
            //右上
            path.lineTo(rect.right - radius, rect.top);
            path.arcTo(new RectF(rect.right - radius2, rect.top, rect.right, rect.top + radius2), -90, 90);
            //右下
            path.lineTo(rect.right, rect.bottom - radius);
            path.arcTo(new RectF(rect.right - radius2, rect.bottom - radius2, rect.right, rect.bottom), 0, 90);
            //左下
            path.lineTo(rect.left + radius, rect.bottom);
            path.arcTo(new RectF(rect.left, rect.bottom - radius2, rect.left + radius2, rect.bottom), 90, 90);
            //左上
            path.lineTo(rect.left, rect.top + radius);
            path.arcTo(new RectF(rect.left, rect.top, rect.left + radius2, rect.top + radius2), 180, 90);
        }
        return path;
    }
}
