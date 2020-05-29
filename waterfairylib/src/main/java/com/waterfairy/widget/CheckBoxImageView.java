package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.waterfairy.library.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/16
 * @Description:
 */

public class CheckBoxImageView extends AppCompatImageView {
    private int mCheckRes, mNoCheckRes;
    private boolean isChecked;
    private OnCheckedListener onCheckedListener;

    public CheckBoxImageView(Context context) {
        this(context, null);
    }

    public CheckBoxImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxImageView);
        mCheckRes = typedArray.getResourceId(R.styleable.CheckBoxImageView_checkRes, 0);
        mNoCheckRes = typedArray.getResourceId(R.styleable.CheckBoxImageView_noCheckRes, 0);
        isChecked = typedArray.getBoolean(R.styleable.CheckBoxImageView_isChecked, false);
        setChecked(isChecked, false);
    }

    public void setChecked(boolean checked, boolean callListener) {
        if (checked) {
            setImageResource(mCheckRes);
            if (callListener && onCheckedListener != null) {
                onCheckedListener.onChecked(this);
            }
        } else {
            setImageResource(mNoCheckRes);
        }
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    public interface OnCheckedListener {
        void onChecked(CheckBoxImageView imageView);
    }
}
