package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.waterfairy.library.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/18
 * @Description:
 */
public class ImageTextView extends AppCompatTextView {
    private Drawable mDrawable;//设置的图片
    private int mScaleWidth; // 图片的宽度
    private int mScaleHeight;// 图片的高度
    private int mPosition;// 图片的位置 1上2左3下4右

    public ImageTextView(Context context) {
        super(context);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ImageTextView);

        mDrawable = typedArray.getDrawable(R.styleable.ImageTextView_drawable);
        mScaleWidth = typedArray
                .getDimensionPixelOffset(
                        R.styleable.ImageTextView_drawableWidth,
                        dip2px(20));
        mScaleHeight = typedArray.getDimensionPixelOffset(
                R.styleable.ImageTextView_drawableHeight,
                dip2px(20));
        mPosition = typedArray.getInt(R.styleable.ImageTextView_position, 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawable != null) {
            mDrawable.setBounds(0, 0, dip2px(mScaleWidth),
                    dip2px(mScaleHeight));

        }
    }

    private int dip2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mPosition) {
            case 1:
                this.setCompoundDrawables(mDrawable, null, null, null);
                break;
            case 2:
                this.setCompoundDrawables(null, mDrawable, null, null);
                break;
            case 3:
                this.setCompoundDrawables(null, null, mDrawable, null);
                break;
            case 4:
                this.setCompoundDrawables(null, null, null, mDrawable);
                break;
            default:
                break;
        }
    }

    /**
     * 设置左侧图片并重绘
     */
    public void setDrawableLeft(Drawable drawable) {
        this.mDrawable = drawable;
        invalidate();
    }

    /**
     * 设置左侧图片并重绘
     */
    public void setDrawableLeft(int drawableRes, Context context) {
        this.mDrawable = context.getResources().getDrawable(drawableRes);
        invalidate();
    }
}
