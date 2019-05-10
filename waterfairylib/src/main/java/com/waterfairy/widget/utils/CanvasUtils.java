package com.waterfairy.widget.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.Log;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 10:48
 * @info:
 */
public class CanvasUtils {
    /**
     * 绘制圆角
     *
     * @param canvas      画笔
     * @param rect        边框
     * @param radius      半径
     * @param strokeWidth 线宽
     * @param strokeColor 线颜色
     * @param solid       背景颜色
     * @param paint       画笔
     */
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
            //solid
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(solid);
            canvas.drawPath(path, paint);
            //stroke
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
            //复原
            if (styleSrc != null)
                paint.setStyle(styleSrc);
            paint.setStrokeWidth(strokeWidthSrc);
        }
    }


    /**
     * 绘制水平文本 附带 颜色条
     * 说明:
     * 以最宽的文本为标准
     *
     * @param canvas
     * @param textListBean
     * @param colors
     * @param paint
     */
    public static void drawHorTextList(Canvas canvas, RectUtils.TextRectFBean textListBean, int[] colors, Paint paint) {
        if (canvas != null && textListBean != null && textListBean.texts != null) {
            if (textListBean.padding < 0) textListBean.padding = 0;
            if (colors == null || colors.length == 0) {
                colors = new int[]{Color.GRAY};
            }
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextSize(textListBean.textSize);
            }

            //源paint 属性
            float strokeWidth = paint.getStrokeWidth();
            int paintColor = paint.getColor();
            float textSize = paint.getTextSize();
            //线高度
            //每行高度
            float perHeight = textListBean.perHeight * (1 + textListBean.textTimes);
            paint.setStrokeWidth(textListBean.perHeight / 4);
            paint.setTextSize(textListBean.textSize);
            //线中心y
            float lineCenterY = 0;
            for (int i = 0; i < textListBean.lineNum; i++) {
                //每行文本y起点 第一行不加倍
                float y = 0;
                if (i == 0) {
                    y = textListBean.rectF.top + textListBean.padding + textListBean.perHeight;
                } else {
                    y = textListBean.rectF.top + textListBean.padding + textListBean.perHeight + perHeight * i;
                }
                //文本底线 - 文本高度的一半
                lineCenterY = y - textListBean.perHeight / 3;
                for (int j = 0; j < textListBean.columnNum; j++) {
                    int pos = i * textListBean.columnNum + j;
                    if (textListBean.texts.size() > pos) {
                        paint.setColor(colors[pos % colors.length]);
                        //获取文本
                        String text = textListBean.texts.get(pos);
                        //线/文本x起点
                        float startX = j * textListBean.perWidth + textListBean.padding + textListBean.rectF.left;
                        if (textListBean.hasColorLine) {
                            //画线
                            paint.setStrokeWidth(textListBean.perHeight / 4);
                            canvas.drawLine(startX, lineCenterY, startX + textListBean.singleTextWidth, lineCenterY, paint);
                            //文本x起点
                            startX += textListBean.singleTextWidth * 1.5F;
                        }
                        paint.setStrokeWidth(strokeWidth);
                        canvas.drawText(text, startX, y, paint);
                    }
                }
            }
            paint.setTextSize(textSize);
            paint.setColor(paintColor);
        }
    }

    /**
     * @param canvas
     * @param mTargetRect
     * @param width
     * @param height
     * @param chartNum
     */
    public static void drawChart(Canvas canvas, Rect mTargetRect, int width, int height, int chartNum) {
        if (width != 0 && height != 0) {
            float left = 0, right = width, top = 0, bottom = height;
            if (mTargetRect != null) {
                left = mTargetRect.left;
                right = mTargetRect.right;
                top = mTargetRect.top;
                bottom = mTargetRect.bottom;
            }
            Paint paintChart = new Paint();
            //网格
            float perHeight = (bottom - top) / chartNum;
            float perWidth = (right - left) / chartNum;

            for (int i = 0; i <= chartNum; i++) {
                if (i % 10 == 0) {
                    paintChart.setColor(Color.GREEN);
                } else if (i % 5 == 0) {
                    paintChart.setColor(Color.BLUE);
                } else {
                    paintChart.setColor(Color.GRAY);
                }
                //x
                canvas.drawLine(left, top + i * perHeight, right, top + i * perHeight, paintChart);
                //y
                canvas.drawLine(left + i * perWidth, top, left + i * perWidth, bottom, paintChart);
            }
        }
    }

    /**
     * 画阴影
     *
     * @param canvas      画布
     * @param radiusBig   最大半径
     * @param shadowWidth 阴影距离
     * @param paint
     */
    public static void drawShadow(Canvas canvas, int radiusBig, int shadowWidth, Paint paint) {
        drawShadow(canvas, radiusBig, shadowWidth, paint, Color.parseColor("#000000"), 8, 120, -120, new float[]{0.2F, 0.5F, 0.8F});
    }

    /**
     * @param canvas      画布
     * @param radiusBig   最大半径
     * @param shadowWidth 阴影距离
     * @param paint       画笔
     * @param shadowColor 阴影颜色
     * @param alphaStart  透明 开始
     * @param alphaEnd    透明 结束
     * @param rotate      旋转角度
     * @param points      位置 大小范围:0-1   根据旋转角度确定具体位置
     */
    public static void drawShadow(Canvas canvas, int radiusBig, int shadowWidth, Paint paint, int shadowColor, int alphaStart, int alphaEnd, int rotate, float[] points) {
        if (canvas == null || radiusBig <= 0 || shadowWidth <= 0) return;
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
        }

        //绘制阴影
        paint.setStyle(Paint.Style.STROKE);
        //旋转
        canvas.rotate(rotate, radiusBig, radiusBig);
        //中心颜色
        for (int i = 0; i < shadowWidth; i++) {

            //过度 radio:  0-1
            float radio = (i + 1) / (float) shadowWidth;

            //alpha 过度  0 - alphaStart
            //alpha 过度  0 - alphaEnd
            int alphaStartTemp = (int) (radio * alphaStart);
            int alphaEndTemp = (int) (radio * alphaEnd);

            //color 过度   colorStart
            //color 过度   colorEnd
            int colorStart = Color.argb(alphaStartTemp, Color.red(shadowColor), Color.green(shadowColor), Color.blue(shadowColor));
            int colorEnd = Color.argb(alphaEndTemp, Color.red(shadowColor), Color.green(shadowColor), Color.blue(shadowColor));

            SweepGradient sweepGradient = new SweepGradient(radiusBig, radiusBig,
                    new int[]{colorStart, colorEnd, colorStart}, points);
            paint.setShader(sweepGradient);
            canvas.drawCircle(radiusBig, radiusBig, radiusBig - i, paint);
        }
    }
}
