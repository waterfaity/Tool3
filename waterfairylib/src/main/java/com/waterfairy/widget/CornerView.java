package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.waterfairy.library.R;
import com.waterfairy.widget.utils.PathUtils;
import com.waterfairy.widget.utils.XFModeUtils;

/**
 * <declare-styleable name="CornerView">
 * <attr name="cornerType" format="integer"/>
 * <attr name="cornerPosition">
 * <enum name="LT" value="0"/>
 * <enum name="RT" value="1"/>
 * <enum name="RB" value="2"/>
 * <enum name="LB" value="3"/>
 * </attr>
 * <attr name="cornerRadius" format="dimension"/>
 * <attr name="cornerColor" format="color"/>
 * </declare-styleable>
 *
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/2/14 11:21
 * @info: 绘制某个角 :描述 :该角圆角 并且 对应一个弧度角
 * - **********
 * -***********
 * -**********
 * -*********
 * -******
 * -***
 * -*
 *
 */
public class CornerView extends View {
    private final Paint paint;
    private int color;//颜色
    private int radius;//圆角半径
    private int pos;//位置  左上 右上 右下 左下
    private RectF rectF;//
    private int type;

    private Bitmap mBitmapBig;//大扇形
    private Bitmap mBitmapLittle;//角上的圆角


    public CornerView(Context context) {
        this(context, null);
    }

    public CornerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerView);
        color = typedArray.getColor(R.styleable.CornerView_cornerColor, Color.GRAY);
        radius = typedArray.getDimensionPixelSize(R.styleable.CornerView_cornerRadius, 0);
        pos = typedArray.getInteger(R.styleable.CornerView_cornerPosition, 0);
        type = typedArray.getInt(R.styleable.CornerView_cornerType, 7);//PorterDuff.Mode.DST_OUT
        typedArray.recycle();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (right - left != 0 || bottom - top != 0) {
            recycle();
            mBitmapBig = newBitmapBig(right - left, bottom - top);
            mBitmapLittle = newBitmapLittle(right - left, bottom - top);
        }
    }

    /**
     * 回收
     */
    private void recycle() {
        if (mBitmapBig != null && !mBitmapBig.isRecycled()) mBitmapBig.recycle();
        if (mBitmapLittle != null && !mBitmapLittle.isRecycled()) mBitmapLittle.recycle();
        mBitmapBig = null;
        mBitmapLittle = null;
    }

    /**
     * 角落圆角
     *
     * @param width
     * @param height
     * @return
     */
    private Bitmap newBitmapLittle(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawPath(PathUtils.getCornerOut(new RectF(0, 0, width, height), radius, pos), paint);
        return bitmap;
    }

    /**
     * 绘制扇形
     *
     * @param width
     * @param height
     * @return
     */
    private Bitmap newBitmapBig(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float startAngle = 0;
        float left = 0, right = 0, top = 0, bottom = 0;
        switch (pos) {
            case 0:
                startAngle = 0;
                left = -width;
                right = width;
                top = -height;
                bottom = height;
                break;
            case 1:
                startAngle = 90;
                left = 0;
                right = width * 2;
                top = -height;
                bottom = height;
                break;
            case 2:
                left = 0;
                top = 0;
                right = width * 2;
                bottom = height * 2;
                startAngle = 180;
                break;
            case 3:
                left = -width;
                bottom = height * 2;
                right = width;
                top = 0;
                startAngle = 270;
                break;
        }

        rectF.set(left, top, right, bottom);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawArc(rectF, startAngle, 90, true, paint);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) return;
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), paint, Canvas.ALL_SAVE_FLAG);
        if (mBitmapLittle == null) mBitmapLittle = newBitmapLittle(getWidth(), getHeight());
        if (mBitmapBig == null) mBitmapBig = newBitmapBig(getWidth(), getHeight());
        canvas.drawBitmap(mBitmapLittle, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(XFModeUtils.getMode(type)));
        canvas.drawBitmap(mBitmapBig, 0, 0, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycle();

    }
}
