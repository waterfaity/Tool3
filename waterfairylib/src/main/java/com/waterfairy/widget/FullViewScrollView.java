package com.waterfairy.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/4 16:38
 * @info:
 */
public class FullViewScrollView extends ScrollView {

    /**
     * Indicates that the pager is in an idle, settled state. The current page
     * is fully in view and no animation is in progress.
     */
    public static final int SCROLL_STATE_IDLE = 0;

    /**
     * Indicates that the pager is currently being dragged by the user.
     */
    public static final int SCROLL_STATE_DRAGGING = 1;

    /**
     * Indicates that the pager is in the process of settling to a final position.
     */
    public static final int SCROLL_STATE_SETTLING = 2;

    private static final String TAG = "fvsv";
    private int currentPosition;
    private int totalNum;
    private int height;
    private boolean autoMove = false;
    private float scrollHeight;
    private LinearLayout contentLin;
    private int lastPosition;
    private Adapter mAdapter;
    float startY = 0, startX;
    float endY = 0, endX;

    public FullViewScrollView(Context context) {
        this(context, null);
    }

    public FullViewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollHeight = context.getResources().getDisplayMetrics().density * 1;
        contentLin = new LinearLayout(context);
        contentLin.setOrientation(LinearLayout.VERTICAL);
        addView(contentLin);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mAdapter != null && changed) {
            Log.i(TAG, "onLayout:初始化布局");
            contentLin.removeAllViews();
            totalNum = mAdapter.getCount();
            for (int i = 0; i < totalNum; i++) {
                View itemView = mAdapter.getView(i, contentLin);
                contentLin.addView(itemView);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.width = r - l;
                layoutParams.height = height = b - t;
                itemView.setLayoutParams(layoutParams);
                if (mAdapter != null) mAdapter.onShowView(itemView, i);
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        handleMotionEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private boolean handleMotionEvent(MotionEvent ev) {
        if (autoMove) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (onPageSelectListener != null) {
                    onPageSelectListener.onPageScrollStateChanged(SCROLL_STATE_SETTLING);
                }
                break;
            case MotionEvent.ACTION_UP:
                endY = ev.getY();
                endX = ev.getX();
                handleEnd();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    private void handleEnd() {
        float dy = endY - startY;
        float dx = endX - startX;
        if (Math.abs(dy) > Math.abs(dx)) {
            if (dy > scrollHeight) {
                //下滑
                moveTo(false);
            } else if (dy < -scrollHeight) {
                //上滑
                moveTo(true);
            }
        }

    }


    private void moveTo(boolean next) {
        if ((currentPosition == 0 && !next) || (currentPosition >= totalNum - 1 && next))
            return;//第一页 或 最后一页
        lastPosition = currentPosition;
        if (next) {
            currentPosition++;
        } else {
            currentPosition--;
        }
        startMove();
    }

    private void startMove() {
        //目标位置
        final int targetScrollY = height * currentPosition;
        //当前位置
        final int currentScrollY = getScrollY();
        //差值
        final int dx = targetScrollY - currentScrollY;

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();
                scrollTo(0, (int) (dx * value + currentScrollY));
                if (value == 0 && onPageSelectListener != null) {
                    //滚动开始
                    onPageSelectListener.onPageScrollStateChanged(SCROLL_STATE_SETTLING);
                }
                int tempDy = (int) (dx * value);
                if (value == 1) {
                    autoMove = false;
                    if (onPageSelectListener != null) {
                        //滚动监听
                        onPageSelectListener.onPageScrolled(currentPosition, tempDy / (float) Math.abs(dx), tempDy);
                        //page选择
                        onPageSelectListener.onPageSelected(currentPosition);
                        //滚动停止
                        onPageSelectListener.onPageScrollStateChanged(SCROLL_STATE_IDLE);
                    }
                } else {
                    if (onPageSelectListener != null) {
                        //滚动监听
                        onPageSelectListener.onPageScrolled(lastPosition, tempDy / (float) Math.abs(dx), tempDy);
                    }
                }
            }
        });
        valueAnimator.setDuration(750);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
        autoMove = true;
    }

    private OnPageChangeListener onPageSelectListener;

    public void setOnPageSelectListener(OnPageChangeListener onPageSelectListener) {
        this.onPageSelectListener = onPageSelectListener;
    }

    public interface OnPageChangeListener {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        void onPageSelected(int position);

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see FullViewScrollView#SCROLL_STATE_IDLE
         * @see FullViewScrollView#SCROLL_STATE_DRAGGING
         * @see FullViewScrollView#SCROLL_STATE_SETTLING
         */
        void onPageScrollStateChanged(int state);
    }

    public int getTotalCount() {
        return totalNum;
    }

    public void setAdapter(Adapter pagerAdapter) {
        mAdapter = pagerAdapter;
        requestLayout();
    }

    public interface Adapter {
        int getCount();

        View getView(int pos, ViewGroup viewGroup);

        void onShowView(View view, int position);
    }
}
