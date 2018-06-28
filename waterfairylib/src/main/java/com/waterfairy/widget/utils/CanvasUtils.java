package com.waterfairy.widget.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 10:48
 * @info:
 */
public class CanvasUtils {
    public static void drawCorner(Canvas canvas, RectF rect, int radius, float strokeWidth, int strokeColor, int solid, Paint paint) {
        if (canvas != null && rect != null) {
            float strokeWidthSrc = 0;
            Paint.Style styleSrc = null;
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(strokeWidth);
            } else {
                strokeWidthSrc = paint.getStrokeWidth();
                styleSrc = paint.getStyle();
                paint.setStrokeWidth(strokeWidth);
            }

            Path path = PathUtils.getCorner(rect, radius);
            //stroke
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
            //solid
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(solid);
            canvas.drawPath(path, paint);
            //复原
            if (styleSrc != null)
                paint.setStyle(styleSrc);
            paint.setStrokeWidth(strokeWidthSrc);
        }
    }

    public static void drawTextList(Canvas canvas, RectF textListRectF, List<String> strings,
                                    int[] colors, float textSize, Paint paint, float padding) {
        drawTextList(canvas, textListRectF, strings, colors, 0, textSize, paint, padding, false);
    }

    public static void drawTextList(Canvas canvas, RectF textListRectF, List<String> strings,
                                    int[] colors,
                                    float textSize, Paint paint,
                                    float padding, boolean hasColorLine) {
        drawTextList(canvas, textListRectF, strings, colors, 0, textSize, paint, padding, hasColorLine);
    }

    /**
     * 绘制 垂直文本
     *
     * @param canvas
     * @param textListRectF
     * @param strings
     * @param colors
     * @param textColor     不设置使用colors
     * @param textSize
     * @param paint
     * @param padding       边距
     * @param hasColorLine  颜色条
     */

    public static void drawTextList(Canvas canvas, RectF textListRectF, List<String> strings,
                                    int[] colors, int textColor,
                                    float textSize, Paint paint,
                                    float padding, boolean hasColorLine) {
        if (canvas != null && textListRectF != null && strings != null && strings.size() > 0) {
            if (padding < 0) padding = 0;
            if (colors == null || colors.length == 0) {
                colors = new int[]{Color.GRAY};
            }
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextSize(textSize);
            }
            float startX = textListRectF.left + padding;
            int textWidth = 0;
            float lineStartX = 0;
            if (hasColorLine) {
                textWidth = RectUtils.getTextRect("正", (int) textSize).width();
                startX = startX + textWidth * 1.5F;
                lineStartX = textListRectF.left + padding;
            }
            //平均高度
            float perHeight = (textListRectF.height() - 2 * padding) / strings.size();
            float strokeWidth = paint.getStrokeWidth();
            for (int i = 0; i < strings.size(); i++) {
                if (textColor != 0) {
                    paint.setColor(textColor);
                } else {
                    paint.setColor(colors[i % colors.length]);
                }
                float y = textListRectF.top + padding + perHeight * (i + 1);
                if (hasColorLine) {
                    paint.setStrokeWidth(textWidth / 4);
                    paint.setColor(colors[i % colors.length]);
                    canvas.drawLine(lineStartX, y - perHeight / 3, lineStartX + textWidth, y - perHeight / 3, paint);
                }
                if (textColor != 0) {
                    paint.setColor(textColor);
                } else {
                    paint.setColor(colors[i % colors.length]);
                }
                paint.setStrokeWidth(strokeWidth);
                canvas.drawText(strings.get(i), startX, y, paint);
            }
        }
    }

    /**
     * 绘制垂直文本 附带 颜色条
     *
     * @param canvas
     * @param textListRectF
     * @param strings
     * @param colors
     * @param textSize
     * @param paint
     * @param padding
     * @param hasColorLine  颜色标识条
     */
    public static void drawTextListHor(Canvas canvas, RectF textListRectF, List<String> strings, int[] colors, float textSize, Paint paint, float padding, boolean hasColorLine) {
        if (canvas != null && textListRectF != null && strings != null && strings.size() > 0) {
            if (padding < 0) padding = 0;
            if (colors == null || colors.length == 0) {
                colors = new int[]{Color.GRAY};
            }
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextSize(textSize);
            }


        }
    }
}
