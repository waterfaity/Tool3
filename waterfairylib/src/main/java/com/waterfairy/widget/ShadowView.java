package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.waterfairy.library.R;
import com.waterfairy.widget.baseView.BaseSelfView;



/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/8
 * @Description:
 */

public class ShadowView extends BaseSelfView {
    private int radius;//过度半径
    private int radiusx2;//直径长度
    private Paint paint;//画笔
    //hasCorner =true
    private RectF ltRectFArc, lbRectFArc, rtRectFArc, rbRectFArc;//1/4圆区域
    RadialGradient ltRadial, lbRadial, rtRadial, rbRadial;//1/4圆过度
    LinearGradient topLinear, rightLinear, bottomLinear, leftLinear;//线性过度 4个方向
    private RectF topRectF, rightRectF, bottomRectF, leftRectF;//线性过度区域
    private RectF centerRect;//中心方块区域
    //hasCorner =false
    private Path leftPath, topPath, rightPath, bottomPath;
    private int startColor = Color.parseColor("#666666");//中心颜色
    private int endColor = Color.TRANSPARENT;//结束颜色
    private int solidColor = startColor;//solid颜色
    private boolean hasCornerTop = true, hasCornerRight = true, hasCornerLeft = true, hasCornerBottom = true;//是否有4个方向的 1/4圆过度
    private boolean hasCorner;

    public ShadowView(Context context) {
        this(context, null);
    }

    public ShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowView);
        radius = typedArray.getDimensionPixelSize(R.styleable.ShadowView_shadowRadius,
                (int) (context.getResources().getDisplayMetrics().density * 20));
        startColor = typedArray.getColor(R.styleable.ShadowView_shadowStartColor, startColor);
        endColor = typedArray.getColor(R.styleable.ShadowView_shadowEndColor, Color.parseColor("#00000000"));
        solidColor = typedArray.getColor(R.styleable.ShadowView_shadowSolidColor, startColor);
        hasCorner = typedArray.getBoolean(R.styleable.ShadowView_hasCorner, true);

        hasCornerTop = typedArray.getBoolean(R.styleable.ShadowView_hasCornerTop, true);
        hasCornerRight = typedArray.getBoolean(R.styleable.ShadowView_hasCornerRight, true);
        hasCornerLeft = typedArray.getBoolean(R.styleable.ShadowView_hasCornerLeft, true);
        hasCornerBottom = typedArray.getBoolean(R.styleable.ShadowView_hasCornerBottom, true);

        typedArray.recycle();
        radiusx2 = 2 * radius;
        onInitDataOk();
    }

    @Override
    protected void beforeDraw() {
        paint = new Paint();
        if (hasCorner) {
            paint.setAntiAlias(true);
            if (mWidth < radiusx2 || mHeight < radiusx2) {
                radius = Math.min(mWidth / 2, mHeight / 2);
                radiusx2 = 2 * radius;
            }

            if (hasCornerTop && hasCornerLeft) {
                ltRadial = new RadialGradient(radius, radius, radius, startColor, endColor, Shader.TileMode.CLAMP);
                ltRectFArc = new RectF(0, 0, radiusx2, radiusx2);
            }

            if (hasCornerTop && hasCornerRight) {
                rtRadial = new RadialGradient(mWidth - radius, radius, radius, startColor, endColor, Shader.TileMode.CLAMP);
                rtRectFArc = new RectF(mWidth - radiusx2, 0, mWidth, radiusx2);
            }

            if (hasCornerBottom && hasCornerRight) {
                rbRadial = new RadialGradient(mWidth - radius, mHeight - radius, radius, startColor, endColor, Shader.TileMode.CLAMP);
                rbRectFArc = new RectF(mWidth - radiusx2, mHeight - radiusx2, mWidth, mHeight);
            }

            if (hasCornerLeft && hasCornerBottom) {
                lbRadial = new RadialGradient(radius, mHeight - radius, radius, startColor, endColor, Shader.TileMode.CLAMP);
                lbRectFArc = new RectF(0, mHeight - radiusx2, radiusx2, mHeight);
            }
//        上
            if (hasCornerTop) {//没有top  对 左右 有影响
                topLinear = new LinearGradient(radius, 0, radius, radius, endColor, startColor, Shader.TileMode.CLAMP);
                topRectF = new RectF(hasCornerLeft ? radius : 0, 0, hasCornerRight ? mWidth - radius : mWidth, radius);
            }
//        右
            if (hasCornerRight) {//没有right 对上下 有影响
                rightLinear = new LinearGradient(mWidth - radius, radius, mWidth, radius, startColor, endColor, Shader.TileMode.CLAMP);
                rightRectF = new RectF(mWidth - radius, hasCornerTop ? radius : 0, mWidth, hasCornerBottom ? mHeight - radius : mHeight);
            }
//        下
            if (hasCornerBottom) {
                bottomLinear = new LinearGradient(radius, mHeight - radius, radius, mHeight, startColor, endColor, Shader.TileMode.CLAMP);
                bottomRectF = new RectF(hasCornerLeft ? radius : 0, mHeight - radius, hasCornerRight ? mWidth - radius : mWidth, mHeight);
            }
//        左
            if (hasCornerLeft) {//对上下
                leftLinear = new LinearGradient(0, radius, radius, radius, endColor, startColor, Shader.TileMode.CLAMP);
                leftRectF = new RectF(0, hasCornerTop ? radius : 0, radius, hasCornerBottom ? mHeight - radius : mHeight);
            }
            centerRect = new RectF(hasCornerLeft ? radius : 0, hasCornerTop ? radius : 0, hasCornerRight ? mWidth - radius : mWidth, hasCornerBottom ? mHeight - radius : mHeight);
        } else {

//            paint.setAntiAlias(true);
            //没有边角(4个三角形区域)-> 梯形区域
            //左
            leftPath = new Path();
            leftPath.moveTo(0, 0);
            leftPath.lineTo(radius, radius);
            leftPath.lineTo(radius, mHeight - radius);
            leftPath.lineTo(0, mHeight);
            leftPath.lineTo(0, 0);
            //上
            topPath = new Path();
            topPath.moveTo(0, 0);
            topPath.lineTo(mWidth, 0);
            topPath.lineTo(mWidth - radius, radius);
            topPath.lineTo(radius, radius);
            topPath.lineTo(0, 0);
            //右
            rightPath = new Path();
            rightPath.moveTo(mWidth, 0);
            rightPath.lineTo(mWidth - radius, radius);
            rightPath.lineTo(mWidth - radius, mHeight - radius);
            rightPath.lineTo(mWidth, mHeight);
            rightPath.lineTo(mWidth, 0);
            //下
            bottomPath = new Path();
            bottomPath.moveTo(0, mHeight);
            bottomPath.lineTo(radius, mHeight - radius);
            bottomPath.lineTo(mWidth - radius, mHeight - radius);
            bottomPath.lineTo(mWidth, mHeight);
            bottomPath.lineTo(0, mHeight);

            leftLinear = new LinearGradient(0, 0, radius, 0, endColor, startColor, Shader.TileMode.CLAMP);
            topLinear = new LinearGradient(0, 0, 0, radius, endColor, startColor, Shader.TileMode.CLAMP);
            rightLinear = new LinearGradient(mWidth - radius, 0, mWidth, 0, startColor, endColor, Shader.TileMode.CLAMP);
            bottomLinear = new LinearGradient(0, mHeight - radius, 0, mHeight, startColor, endColor, Shader.TileMode.CLAMP);

            centerRect = new RectF(radius, radius, mWidth - radius, mHeight - radius);
        }
    }

    public void initCorner(boolean top, boolean right, boolean left, boolean bottom) {
        this.hasCornerTop = top;
        this.hasCornerRight = right;
        this.hasCornerLeft = left;
        this.hasCornerBottom = bottom;
    }

    public void initColor(int centerColor, int endColor) {
        this.startColor = centerColor;
        this.endColor = endColor;
    }

    public void initRadius(int radius) {
        this.radius = radius;
        this.radiusx2 = 2 * radius;
    }

    public void initOk() {
        onInitDataOk();
    }

    @Override
    protected void drawOne(Canvas canvas) {
        if (hasCorner) {
            if (hasCornerTop && hasCornerLeft) {
                paint.setShader(ltRadial);
                canvas.drawArc(ltRectFArc, 180, 90, true, paint);
            }

            if (hasCornerTop && hasCornerRight) {
                paint.setShader(rtRadial);
                canvas.drawArc(rtRectFArc, -90, 90, true, paint);
            }

            if (hasCornerBottom && hasCornerRight) {
                paint.setShader(rbRadial);
                canvas.drawArc(rbRectFArc, 0, 90, true, paint);
            }

            if (hasCornerLeft && hasCornerBottom) {
                paint.setShader(lbRadial);
                canvas.drawArc(lbRectFArc, 90, 90, true, paint);
            }

            if (hasCornerTop) {
                paint.setShader(topLinear);
                canvas.drawRect(topRectF, paint);
            }

            if (hasCornerRight) {
                paint.setShader(rightLinear);
                canvas.drawRect(rightRectF, paint);
            }

            if (hasCornerBottom) {
                paint.setShader(bottomLinear);
                canvas.drawRect(bottomRectF, paint);
            }

            if (hasCornerLeft) {
                paint.setShader(leftLinear);
                canvas.drawRect(leftRectF, paint);
            }

        } else {
            paint.setShader(leftLinear);
            canvas.drawPath(leftPath, paint);

            paint.setShader(topLinear);
            canvas.drawPath(topPath, paint);

            paint.setShader(rightLinear);
            canvas.drawPath(rightPath, paint);

            paint.setShader(bottomLinear);
            canvas.drawPath(bottomPath, paint);
        }
        //画中心
        paint.setShader(null);
        paint.setColor(solidColor);
        canvas.drawRect(centerRect, paint);
    }
}
