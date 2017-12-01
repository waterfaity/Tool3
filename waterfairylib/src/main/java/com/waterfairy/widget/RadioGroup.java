package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.waterfairy.library.R;


/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/9/25
 * @Description:
 */

public class RadioGroup extends LinearLayout implements ScaleImageView.OnScaleViewCheckListener {
    private int checkIndex;
    private ScaleImageView checkedView;
    private OnCheckListener onCheckListener;

    public RadioGroup(Context context) {
        super(context);
        init();
    }

    public RadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
            checkIndex = typedArray.getInt(R.styleable.RadioGroup_checkIndex, -1);
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        super.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View childView) {
                Log.i("test", "onChildViewAdded: ");
                if (childView instanceof ScaleImageView) {
                    ScaleImageView scaleImageView = ((ScaleImageView) childView)
                            .setOnCheckListener(RadioGroup.this);

                    if ((checkedView == null && checkIndex == -1) ||
                            (checkIndex != -1 && scaleImageView.getIndex() == checkIndex)) {
                        checkedView = (ScaleImageView) childView;
                        ((ScaleImageView) childView).setCheckedNoListener(true);
                        if (onCheckListener != null)
                            onCheckListener.onChecked(checkedView, checkedView.getIndex());
                    }
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                Log.i("test", "onChildViewRemoved: ");
            }
        });
    }

    @Override
    public void onScaleViewChecked(ScaleImageView view, boolean checked) {
        if (checkedView != null) checkedView.setCheckedNoListener(false);
        view.setCheckedNoListener(true);
        checkedView = view;
        if (onCheckListener != null)
            onCheckListener.onChecked(checkedView, checkedView.getIndex());
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckListener {
        void onChecked(ScaleImageView scaleImageView, int pos);
    }
}
