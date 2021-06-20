package com.waterfairy.widget.paintboard;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 18:20
 * @info:
 */
public class EraserGraph extends Graph {
    private final Paint paint;
    private final Path path;

    private final int strokeWidth;

    public EraserGraph(int strokeWidth) {
        this.strokeWidth = strokeWidth;

        //画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(0);
        paint.setStrokeWidth(strokeWidth);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //路径
        path = new Path();
    }

    @Override
    public String getGraphType() {
        return "eraser";
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
        return new EraserGraph(strokeWidth);
    }
}
