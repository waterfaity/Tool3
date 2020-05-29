package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waterfairy.library.R;


/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/5/9 16:12
 * @info:
 */
public class ImageTextView extends LinearLayout implements View.OnClickListener {
    //data
    private int textColor;
    private String text;
    private int textSize;
    private int marginLeft;
    private int width, height;
    private boolean isChecked = true;
    private int imgRes;
    private int imgResNoChecked;
    //view
    private TextView textView;
    private ImageView imageView;
    //listener
    private OnCheckedListener onCheckedListener;
    private OnClickListener onClickListener;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ImageTextView);
        marginLeft = typedArray.getDimensionPixelSize(R.styleable.ImageTextView_drawableMarginLeft, 0);
        imgRes = typedArray.getResourceId(R.styleable.ImageTextView_drawable, 0);
        imgResNoChecked = typedArray.getResourceId(R.styleable.ImageTextView_drawableNoChecked, 0);
        width = typedArray.getDimensionPixelSize(R.styleable.ImageTextView_drawableWidth, 0);
        height = typedArray.getDimensionPixelSize(R.styleable.ImageTextView_drawableHeight, 0);
        text = typedArray.getString(R.styleable.ImageTextView_text);
        textSize = typedArray.getDimensionPixelSize(R.styleable.ImageTextView_textSize, (int) (14 * getResources().getDisplayMetrics().density));
        textColor = typedArray.getColor(R.styleable.ImageTextView_textColor, Color.parseColor("#454545"));
        isChecked = typedArray.getBoolean(R.styleable.ImageTextView_checked, true);

        typedArray.recycle();
        addView();
        setOnClickListener(this, true);
    }

    private void addView() {
        textView = new TextView(getContext());
        imageView = new ImageView(getContext());
        addView(textView);
        addView(imageView);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setText(text);
        textView.setTextColor(textColor);
        LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.leftMargin = marginLeft;
        imageView.setBackgroundResource(isChecked ? imgRes : imgResNoChecked);
        imageView.setLayoutParams(layoutParams);
    }

    private void setOnClickListener(OnClickListener onClickListener, boolean in) {
        if (in) {
            super.setOnClickListener(onClickListener);
        } else {
            this.onClickListener = onClickListener;
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        setOnClickListener(onClickListener, false);
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }


    @Override
    public void onClick(View v) {
        setCheckState(true);
    }

    private void setCheckState(boolean checkedListener) {
        if (onClickListener != null) onClickListener.onClick(this);
        if (onCheckedListener != null) {
            if (isChecked) {
                imageView.setBackgroundResource(imgResNoChecked);
            } else {
                imageView.setBackgroundResource(imgRes);
            }
            isChecked = !isChecked;
            if (checkedListener)
                onCheckedListener.onChecked(this, isChecked);
        }
    }

    public void setChecked(boolean checked, boolean checkedListener) {
        isChecked = !checked;
        setCheckState(checkedListener);
    }

    public interface OnCheckedListener {
        void onChecked(ImageTextView imageTextView, boolean isChecked);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }
}
