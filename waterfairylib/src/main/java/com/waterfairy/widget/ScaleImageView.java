package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.waterfairy.library.R;


/**
 * Created by water_fairy on 2017/8/30.
 * 995637517@qq.com
 * 功能
 * 1.
 */

public class ScaleImageView extends AppCompatImageView {
    private OnLongClickListener onLongClickListener;
    private static final String TAG = "scaleImageView";
    private int widthRatio, heightRatio;//宽比例/高比例
    private boolean heightStandard = true;//高度为标准
    private boolean hasFilter;//滤镜
    private boolean hasFilterAlpha;//滤镜 透明度
    private int checkResId;//选中图片的资源id
    private int checkNoResId;//未选中图片的资源id
    private OnClickListener onClickListener;
    private OnScaleViewClickListener onScaleViewClickListener;
    private boolean isChecked;//是否旋选中
    public static final int TYPE_NO = 0;
    public static final int TYPE_RADIO = 1;
    public static final int TYPE_CHECKBOX = 2;
    private int type = TYPE_NO;//0,radio;1,checkBox;
    private OnScaleViewCheckListener onScaleViewCheckListener;
    private boolean requestTouch;
    //    private Paint filterPaint;
    private int shadowColor = Color.parseColor("#99999999");
    //    private PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_OVER;
//    private PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_ATOP;
//    private XfermodeHolder xfermodeHolder;
    private int index;//下标,作为radio/checkbox
    private boolean canClick = true;//是否可以点击
    private float alpha = 1f;//
    private float filterAlphaValue = 0.4f;
    private int width, height;
    private String mMark;//标记
    private int startX;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
            widthRatio = typedArray.getInt(R.styleable.ScaleImageView_widthRatio, 0);
            heightRatio = typedArray.getInt(R.styleable.ScaleImageView_heightRatio, 0);
            heightStandard = typedArray.getBoolean(R.styleable.ScaleImageView_heightStandard, true);
            hasFilter = typedArray.getBoolean(R.styleable.ScaleImageView_setFilter, false);
            hasFilterAlpha = typedArray.getBoolean(R.styleable.ScaleImageView_setFilterAlpha, false);
            mMark = typedArray.getString(R.styleable.ScaleImageView_mark);
            if (hasFilter) requestTouch = true;
            checkResId = typedArray.getResourceId(R.styleable.ScaleImageView_checkSrc, 0);
            checkNoResId = typedArray.getResourceId(R.styleable.ScaleImageView_checkNoSrc, 0);
            index = typedArray.getInt(R.styleable.ScaleImageView_index, -1);
            type = typedArray.getInt(R.styleable.ScaleImageView_checkType, 0);
            filterAlphaValue = typedArray.getFloat(R.styleable.ScaleImageView_filterAlphaValue, filterAlphaValue);
            if (type == TYPE_RADIO || type == TYPE_CHECKBOX || hasFilter || hasFilterAlpha)
                requestTouch = true;
            typedArray.recycle();
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (widthRatio != 0 && heightRatio != 0) {
//            int width = MeasureSpec.getSize(widthMeasureSpec);
//            int height = MeasureSpec.getSize(heightMeasureSpec);
//
//            int mode = MeasureSpec.getMode(heightMeasureSpec);
//            Log.i(TAG, "onMeasure: mode :   " + mode);
//
//            if (heightStandard) {
//                width = (int) (widthRatio / (float) heightRatio * height);
//            } else {
//                height = (int) (heightRatio / (float) widthRatio * width);
//            }
//            setMeasuredDimension(width, height);
////            Log.i(TAG, "onMeasure: " + width + "--" + height);
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        if (width != 0 && !heightStandard) {
            height = (int) (heightRatio / (float) widthRatio * width);
            setMeasuredDimension(width, height);
            this.width = width;
            this.height = height;
        } else if (height != 0 && heightStandard) {
            width = (int) (widthRatio / (float) heightRatio * height);
            setMeasuredDimension(width, height);
            this.width = width;
            this.height = height;
        }
//        Log.i(TAG, "onLayout: " + width + "--" + height + "--" + heightStandard + "--" + widthRatio + "--" + heightRatio + "--" + mMark);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec > 0 && !heightStandard) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = (int) (heightRatio / (float) widthRatio * this.width);
        } else if (heightMeasureSpec > 0 && heightStandard) {
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = (int) (widthRatio / (float) heightRatio * height);
        }
        if (this.width != 0 && this.height != 0) {
            setMeasuredDimension(width, height);

//            Log.i(TAG, "onMeasure: " + width + "--" + height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
//        Log.i(TAG, "onMeasure: " + width + "--" + height + "--" + heightStandard + "--" + widthRatio + "--" + heightRatio + "--" + mMark);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (requestTouch && canClick)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    if (hasFilter) {
//                        setColorFilter2(new XfermodeHolder(DEFAULT_MODE, shadowColor));
                        setColorFilter(shadowColor);
                    }
                    if (hasFilterAlpha) {
                        super.setAlpha(filterAlphaValue);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    float upX = event.getX();
                    float upY = event.getY();
                    if (upX >= 0 && upY >= 0 && upX <= width && upY <= height
                            || (upX >= 0 && upY >= 0 && width == 0 && upY <= height)
                            || (upX >= 0 && upY >= 0 && upX <= width && height == 0)) {
                        if (onClickListener != null)
                            onClickListener.onClick(this);
                        if (onScaleViewClickListener != null)
                            onScaleViewClickListener.onScaleViewClick(this);
                        //radio / checkBox
                        if (type == TYPE_RADIO || type == TYPE_CHECKBOX) {
                            if (!isChecked || type == TYPE_CHECKBOX) {
                                setCheckedNoListener(!isChecked);
                                if (onScaleViewCheckListener != null) {
                                    onScaleViewCheckListener.onScaleViewChecked(this, isChecked);
                                }
                            }
                        }
                    }
                    setAlpha(alpha);
//                    setColorFilter2(null);
                    setColorFilter(null);


                    break;
                case MotionEvent.ACTION_CANCEL:
                    setAlpha(alpha);
//                    setColorFilter2(null);
                    setColorFilter(null);

                    break;
            }
        return super.onTouchEvent(event);
    }

    /**
     * 设置radio/checkBox 无监听
     *
     * @param checked
     */
    public void setCheckedNoListener(boolean checked) {
        setChecked(checked, false);
    }

    /**
     * 设置radio/checkBox  有监听
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    /**
     * 设置radio/checkBox
     *
     * @param checked
     * @param hasListener 是否有监听
     */
    private void setChecked(boolean checked, boolean hasListener) {
        isChecked = checked;
        if (isChecked) {
            setImageBitmap(BitmapFactory.decodeResource(getResources(), checkResId));
        } else {
            setImageBitmap(BitmapFactory.decodeResource(getResources(), checkNoResId));
        }
        if (hasListener && onScaleViewCheckListener != null) {
            onScaleViewCheckListener.onScaleViewChecked(this, checked);
        }
    }

    Drawable drawable;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (xfermodeHolder != null)
//            canvas.drawPaint(xfermodeHolder.getPaint());
    }


    /**
     * 是否被选择
     *
     * @return
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * 点击监听
     *
     * @param onClickListener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        requestTouch = true;
        this.onClickListener = onClickListener;
    }

    /**
     * 点击监听
     *
     * @param onScaleViewClickListener
     */
    public void setOnScaleViewClickListener(OnScaleViewClickListener onScaleViewClickListener) {
        requestTouch = true;
        this.onScaleViewClickListener = onScaleViewClickListener;
    }

    /**
     * 设置radio/checkBox 监听
     *
     * @param onScaleViewCheckListener
     * @return
     */
    public ScaleImageView setOnCheckListener(OnScaleViewCheckListener onScaleViewCheckListener) {
        requestTouch = true;
        this.onScaleViewCheckListener = onScaleViewCheckListener;
        return this;
    }

    public void setCanClick(boolean canClick) {
        this.canClick = canClick;
    }

//    private void setColorFilter2(XfermodeHolder xfermodeHolder) {
//        this.xfermodeHolder = xfermodeHolder;
//        invalidate();
//    }

    /**
     * radio/checkBox 监听
     */
    public interface OnScaleViewCheckListener {
        void onScaleViewChecked(ScaleImageView scaleImageView, boolean checked);
    }

    public interface OnScaleViewClickListener {
        void onScaleViewClick(ScaleImageView scaleImageView);
    }

    //    class XfermodeHolder {
//        private Paint paint;
//
//        public XfermodeHolder(PorterDuff.Mode xfermode, int color) {
//            this.xfermode = xfermode;
//            this.color = color;
//        }
//
//        private PorterDuff.Mode xfermode;
//        private int color;
//
//        public PorterDuff.Mode getXfermode() {
//            return xfermode;
//        }
//
//        public void setPorterDuffXfermode(Xfermode mode) {
//            this.xfermode = xfermode;
//        }
//
//        public int getColor() {
//            return color;
//        }
//
//        public void setColor(int color) {
//            this.color = color;
//        }
//
//        public Paint getPaint() {
//            paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setXfermode(new PorterDuffXfermode(xfermode));
//            paint.setColor(color);
//            return paint;
//        }
//    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        super.setAlpha(alpha);
    }

    private Object object;

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }


}
