package com.waterfairy.widget.colorSeclect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.waterfairy.widget.baseView.BaseSelfViewGroup;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by water_fairy on 2017/7/26.
 * 995637517@qq.com
 */

public class ColorSelectView extends BaseSelfViewGroup implements ColorTransitionView.OnRadioChangeListener {
    private Context context;
    private AttributeSet att;
    private ImageView selectColorView;//展示选中的view
    private float density;
    private ColorTransitionView colorTransitionViewDeep, colorTransitionViewAlpha;
    private ColorCircleView colorCircleView;
    private LinearLayout contentView;
    private int padding;
    private OnColorSelectListener onColorSelectListener;


    public ColorSelectView(Context context) {
        this(context, null);
    }

    public ColorSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.att = attrs;
        density = context.getResources().getDisplayMetrics().density;
        padding = (int) (10 * density);
        setPadding(padding, padding, padding, padding);
        contentView = new LinearLayout(context);
        contentView.setOrientation(VERTICAL);
        addView(contentView);
        onInitDataOk();
    }

    @Override
    protected void onGetDimen() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
        layoutParams.height = mHeight;
        layoutParams.width = mWidth - 2 * padding;
        contentView.setLayoutParams(layoutParams);

        colorCircleView = new ColorCircleView(context);
        colorTransitionViewDeep = new ColorTransitionView(context);
        colorTransitionViewAlpha = new ColorTransitionView(context);

        colorCircleView.setOnColoSelectListener(onColorSelectListener);

        contentView.addView(colorCircleView);
        contentView.addView(colorTransitionViewDeep);
        contentView.addView(colorTransitionViewAlpha);
        LinearLayout.LayoutParams layoutParamsColorCircleView = (LinearLayout.LayoutParams) colorCircleView.getLayoutParams();
        layoutParamsColorCircleView.width = mWidth - 2 * padding;
        layoutParamsColorCircleView.height = mWidth - 2 * padding;
        colorCircleView.setLayoutParams(layoutParamsColorCircleView);

        LinearLayout.LayoutParams colorTransitionViewDeepLayoutParams = (LinearLayout.LayoutParams) colorTransitionViewDeep.getLayoutParams();
        colorTransitionViewDeepLayoutParams.width = mWidth - 2 * padding;
        colorTransitionViewDeepLayoutParams.height = mWidth / 6;
        colorTransitionViewDeepLayoutParams.topMargin = padding;
        colorTransitionViewDeep.setLayoutParams(colorTransitionViewDeepLayoutParams);
        colorTransitionViewAlpha.setLayoutParams(colorTransitionViewDeepLayoutParams);
        colorTransitionViewDeep.setStyle(ColorTransitionView.STYLE_DEEP);
        colorTransitionViewAlpha.setStyle(ColorTransitionView.STYLE_ALPHA);
        colorTransitionViewAlpha.setOnRadioChangeListener(this);
        colorTransitionViewDeep.setOnRadioChangeListener(this);

    }

    @Override
    public void onRadioChange(ColorTransitionView colorTransitionView, float radio) {
        if (colorTransitionView.getStyle() == ColorTransitionView.STYLE_DEEP) {
            colorCircleView.updateDeep(radio);
        } else {
            colorCircleView.updateAlpha(radio);
        }
    }

    public void setOnColorSelectListener(OnColorSelectListener onColorSelectListener) {
        this.onColorSelectListener = onColorSelectListener;
    }
}

