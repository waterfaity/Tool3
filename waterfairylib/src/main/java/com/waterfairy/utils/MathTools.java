package com.waterfairy.utils;

import com.waterfairy.widget.baseView.Coordinate;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/2
 * @Description:
 */

public class MathTools {
    private static final String TAG = "mathTools";

    /**
     * 功能：判断点是否在多边形内 方法：求解通过该点的水平线与多边形各边的交点 结论：单边交点为奇数，成立!
     *
     * @param point   指定的某个点
     * @param APoints 多边形的各个顶点坐标（首末点可以不一致）
     * @return
     */
    public static boolean pointInPolygon(Coordinate point, List<Coordinate> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++) {
            Coordinate p1 = APoints.get(i);
            Coordinate p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.getY() == p2.getY()) // p1p2 与 y=p0.y平行
                continue;
            if (point.getY() < Math.min(p1.getY(), p2.getY())) // 交点在p1p2延长线上
                continue;
            if (point.getY() >= Math.max(p1.getY(), p2.getY())) // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标
            // --------------------------------------------------------------
            double x = (double) (point.getY() - p1.getY())
                    * (double) (p2.getX() - p1.getX())
                    / (double) (p2.getY() - p1.getY()) + p1.getX();
            if (x > point.getX())
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }

    /**
     * 某个点target 绕圆点circlePoint 旋转角度transDegree 之后的坐标
     * 假设对图片上任意点(x,y)，绕一个坐标点(rx0,ry0)逆时针旋转a角度后的新的坐标设为(x0, y0)，有公式：
     * x0= (x - rx0)*cos(a) - (y - ry0)*sin(a) + rx0 ;
     * y0= (x - rx0)*sin(a) + (y - ry0)*cos(a) + ry0 ;
     *
     * @param target      目标点
     * @param circlePoint 旋转中心点
     * @param transDegree 旋转角度
     * @return 结果点
     */
    public static Coordinate transPoint2(Coordinate target, Coordinate circlePoint, float transDegree) {
        double sin = Math.sin(Math.toRadians(transDegree));
        double cos = Math.cos(Math.toRadians(transDegree));
        float x = (float) ((target.x - circlePoint.x) * cos - (target.y - circlePoint.y) * sin + circlePoint.x);
        float y = (float) ((target.x - circlePoint.x) * sin + (target.y - circlePoint.y) * cos + circlePoint.y);
        return new Coordinate(x, y);
    }
}
