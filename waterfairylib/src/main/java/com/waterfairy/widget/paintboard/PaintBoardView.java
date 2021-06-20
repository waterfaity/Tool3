package com.waterfairy.widget.paintboard;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 17:03
 * @info:
 */
public class PaintBoardView extends View {

    //绘制历史
    private final List<Graph> historyGraphs = new ArrayList<>();
    //当前所有绘制
    private final List<Graph> graphs = new ArrayList<>();
    //当前绘制
    private Graph graph;

    public PaintBoardView(Context context) {
        this(context, null);
    }

    public PaintBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * 设置绘制类型
     *
     * @param graph
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (graph != null) {
                    try {

                        //移除后面的历史
                        int removeSize = historyGraphs.size() - graphs.size();
                        for (int i = 0; i < removeSize; i++) {
                            historyGraphs.remove(graphs.size() + i);
                        }

                        graph = (Graph) graph.clone();
                        graph.onDown(event);
                        graphs.add(graph);
                        historyGraphs.add(graph);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (graph != null)
                    graph.onMove(event);
                break;
            case MotionEvent.ACTION_UP:
                if (graph != null)
                    graph.onUp(event);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < graphs.size(); i++) {
            graphs.get(i).onDraw(canvas);
        }
    }

    /**
     * 前进
     *
     * @return
     */
    public boolean forward() {
        boolean canForward;
        if (canForward = canForward()) {
            graphs.add(historyGraphs.get(graphs.size()));
            invalidate();
        }
        return canForward;
    }

    /**
     * 返回
     *
     * @return
     */
    public boolean back() {
        boolean canBack;
        if (canBack = canBack()) {
            graphs.remove(graphs.size() - 1);
            invalidate();
        }
        return canBack;
    }

    /**
     * 是否可以返回
     *
     * @return
     */
    public boolean canBack() {
        return graphs.size() > 0;
    }

    /**
     * 是否可以前进
     *
     * @return
     */
    public boolean canForward() {
        return graphs.size() < historyGraphs.size();
    }

    /**
     * 是否绘制
     *
     * @return
     */
    public boolean hasPaint() {
        return canBack();
    }
}
