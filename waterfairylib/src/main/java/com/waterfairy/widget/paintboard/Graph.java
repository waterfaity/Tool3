package com.waterfairy.widget.paintboard;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 17:15
 * @info: 绘制基类
 */
public abstract class Graph implements Cloneable {

    protected abstract String getGraphType();

    protected abstract void onDown(MotionEvent event);

    protected abstract void onMove(MotionEvent event);

    protected abstract void onUp(MotionEvent event);

    protected abstract void onDraw(Canvas canvas);

    @Override
    protected abstract Object clone() throws CloneNotSupportedException;

}
