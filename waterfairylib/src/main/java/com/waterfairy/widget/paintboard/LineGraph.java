package com.waterfairy.widget.paintboard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 17:16
 * @info:
 */
public class LineGraph extends Graph {
    private final Paint paint;
    private final Path path;

    private final int color;
    private final int strokeWidth;

    public LineGraph(int color, int strokeWidth) {
        this.color = color;
        this.strokeWidth = strokeWidth;

        //画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        //路径
        path = new Path();
    }

    @Override
    public String getGraphType() {
        return "line";
    }

    @Override
    protected void onDown(MotionEvent event) {
        path.moveTo(event.getX(), event.getY());
    }

    @Override
    public void onMove(MotionEvent event) {
        path.lineTo(event.getX(), event.getY());
    }

    @Override
    public void onUp(MotionEvent event) {

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new LineGraph(color, strokeWidth);
    }
}
