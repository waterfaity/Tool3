package com.waterfairy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/23
 * @Description: 添加多个view  超过 边界  换行
 */
public class TagLayoutView extends ViewGroup {
    private List<int[]> childLocation;//子view  layout       l,   t,   r,   b

    public TagLayoutView(Context context) {
        this(context, null);
    }

    public TagLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        childLocation = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childLocation.clear();
        int childCount = getChildCount();
        int totalHeight = 0, totalWidth = 0;
        int childWidth = 0, childHeight = 0;
        int rootViewWidth = getMeasuredWidth();

        int maxHeightInLine = 0;
        for (int i = 0; i < childCount; i++) {
            //获取子view
            View childView = getChildAt(i);
            //获取view的宽高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            childHeight = childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            childWidth = childView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            //是否超出边界
            if (totalWidth + childWidth > rootViewWidth) {
                //换行
                totalWidth = 0;//未计算 当前view的高度
                totalHeight += maxHeightInLine;
                maxHeightInLine = childHeight;
            } else {
                //计算单行view的最高值
                if (maxHeightInLine < childHeight) maxHeightInLine = childHeight;
            }
            childLocation.add(new int[]{totalWidth + layoutParams.leftMargin, totalHeight + layoutParams.topMargin, totalWidth + childWidth - layoutParams.rightMargin, totalHeight + childHeight - layoutParams.bottomMargin});
            totalWidth += childWidth;
            if (i == childCount - 1) {
                totalHeight += maxHeightInLine;
            }
        }

        //计算布局总宽/高度
        int height = 0;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = totalHeight;
        }
        int width = 0;
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = rootViewWidth;
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 这个一定要设置，否则会包强转错误
     * 设置它支持 marginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int[] location = childLocation.get(i);
            view.layout(location[0], location[1], location[2], location[3]);
        }
    }
}
