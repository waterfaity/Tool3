package com.waterfairy.widget.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 13:27
 * @info:
 */
public class RectUtils {
    /**
     * 获取文本边框
     *
     * @param content
     * @param textSize
     * @return
     */
    public static Rect getTextRect(String content, int textSize) {
        Rect rect = new Rect();
        if (TextUtils.isEmpty(content) || textSize <= 0) return rect;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect;
    }

    public static RectF getTextListRectF(List<String> texts, float textSize, float textTimes, float padding) {
        return getTextListRectF(texts, textSize, textTimes, padding, false);
    }

    /**
     * 获取 list 文本边框
     *
     * @param texts
     * @param textSize
     * @param textTimes
     * @param padding      边框距离
     * @param hasColorLine 画标识线
     * @return
     */
    public static RectF getTextListRectF(List<String> texts, float textSize, float textTimes, float padding, boolean hasColorLine) {
        if (texts == null || texts.size() == 0) return null;
        if (padding < 0) padding = 0;
        int maxWidth = 0;
        int maxHeight = 0;
        int tempWidth = 0;
        if (hasColorLine) {
            tempWidth = (int) (getTextRect("正", (int) textSize).width() * 1.5F);
        }
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            Rect textRect = getTextRect(text, (int) textSize);
            int width = textRect.right + textRect.left;
            int height = textRect.bottom - textRect.top;
            maxWidth = Math.max(width, maxWidth);
            maxHeight = Math.max(height, maxHeight);
        }
        return new RectF(0, 0, maxWidth + 2 * padding + tempWidth, maxHeight * (1 + textTimes) * texts.size() + 2 * padding);
    }

    public static RectF getTextListRectF(RectF rectF, float centerX, float centerY) {
        return getTextListRectF(rectF, centerX, centerY, 0, 0, 0, 0);
    }

    /**
     * 有参照物的边框的转换
     *
     * @param centerX 将要显示的中心
     * @param centerY
     * @param left    边界
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public static RectF getTextListRectF(RectF rectF,
                                         float centerX, float centerY,
                                         int left, int top, int right, int bottom) {
        //width
        float textWidth = rectF.width();
        float halfWidth = textWidth / 2;
        //height
        float textHeight = rectF.height();
        float halfHeight = textHeight / 2;

        float textLeft = 0, textRight = 0;
        if (right - left == 0) {
            textLeft = centerX - halfWidth;
            textRight = centerX + halfWidth;
        } else if (centerX - left < halfWidth) {
            textLeft = left;
            textRight = (int) (left + textWidth);
        } else if (right - centerX < halfWidth) {
            textRight = right;
            textLeft = right - textWidth;
        } else {
            textLeft = centerX - halfWidth;
            textRight = centerX + halfWidth;
        }

        float textTop = 0, textBottom = 0;
        if (bottom - top == 0) {
            textTop = centerY - halfHeight;
            textBottom = centerY + halfHeight;
        } else if (centerY - top < halfHeight) {
            textTop = top;
            textBottom = (int) (top + textHeight);
        } else if (bottom - centerY < halfHeight) {
            textBottom = bottom;
            textTop = bottom - textHeight;
        } else {
            textTop = centerY - halfHeight;
            textBottom = centerY + halfHeight;
        }
        return new RectF(textLeft, textTop, textRight, textBottom);
    }
}
