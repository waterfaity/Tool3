package com.waterfairy.widget.moveView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/8/30 16:43
 * @info: 拖动到指定点消失
 */
public class PullImageMoreView extends BaseMoveSelfView {
    private static final String TAG = "PullImageMoreView";

    private RectF areaRectTemp;
    private Rect areaRect;
    private ValueAnimator dismissValueAnimator;
    private boolean showDismissPoint;

    public PullImageMoreView(Context context) {
        super(context);
    }

    public PullImageMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isShowDismissPoint() {
        return showDismissPoint;
    }

    public void setShowDismissPoint(boolean showDismissPoint) {
        this.showDismissPoint = showDismissPoint;
    }

    /**
     * 设置消失的区域  基于 bg img 如果没有bg 基于 view
     *
     * @param areaRect
     */
    public void setDismissArea(RectF areaRect) {
        this.areaRectTemp = areaRect;
    }

    protected void destroyAnim() {
        super.destroyAnim();
        if (dismissValueAnimator != null) {
            dismissValueAnimator.cancel();
            dismissValueAnimator = null;
        }
    }

    /**
     * 消失动画
     */
    private void startDismissAnim() {
        if (dismissValueAnimator == null) {
            dismissValueAnimator = new ValueAnimator();
            dismissValueAnimator.setDuration(400);
            dismissValueAnimator.setFloatValues(1F);
            dismissValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float value = (float) animation.getAnimatedValue();
                    Rect moveRectCopy = currentImageBean.getMoveRectCopy();
                    int widthTemp = (int) (moveRectCopy.width() * value);
                    int heightTemp = (int) (moveRectCopy.height() * value);

                    currentImageBean.rectFMove.left = moveRectCopy.left + widthTemp;
                    currentImageBean.rectFMove.top = moveRectCopy.top + heightTemp;
                    currentImageBean.rectFMove.right = moveRectCopy.right - widthTemp;
                    currentImageBean.rectFMove.bottom = moveRectCopy.bottom - heightTemp;

                    invalidate();
                    if (value >= 1F) {
                        onDismissAnimEnd();
                    }
                }
            });
        }
        isAnimRunning = true;
        dismissValueAnimator.start();
    }

    private void onDismissAnimEnd() {
        currentImageBean.isShow = false;
        currentImageBean.rectFMoveCopy = null;
        isAnimRunning = false;
        if (onDismissListener != null && srcData != null) {
            int dismissCount = 0;
            for (BaseImageBean imageBean : srcData) {
                if (!imageBean.isShow) {
                    dismissCount++;
                }
            }
            onDismissListener.onDismiss(this, srcData.length, dismissCount, currentPos);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (areaRect != null && showDismissPoint) {
            canvas.drawCircle(areaRect.centerX(), areaRect.centerY(), 10, new Paint());
        }
    }

    @Override
    public void onViewMeasure(boolean changed, int width, int height) {
        if (!hasInit) {
            onCalc();
        }
    }

    @Override
    protected boolean handleUpAction(MotionEvent event) {
        boolean handle = false;
        if (areaRect != null) {
            int centerX = areaRect.centerX();
            int centerY = areaRect.centerY();
            if (centerX > currentImageBean.rectFMove.left &&
                    centerX < currentImageBean.rectFMove.right &&
                    centerY > currentImageBean.rectFMove.top &&
                    centerY < currentImageBean.rectFMove.bottom) {
                startDismissAnim();
                handle = true;
            }
        }
        return handle;
    }

    protected void onCalc() {
        super.onCalc();
        //计算消失区域
        if (bgImageBean != null && bgImageBean.rectFSrc != null) {
            //由背景图计算
            calcAreaTemp(bgImageBean.rectFSrc);
        } else {
            //由view计算
            calcAreaTemp(new Rect(0, 0, width, height));
        }
    }

    private void calcAreaTemp(Rect rect) {
        if (rect != null && areaRectTemp != null) {
            int width = rect.width();
            int height = rect.height();

            int left = (int) (rect.left + areaRectTemp.left * width);
            int top = (int) (rect.top + areaRectTemp.top * height);
            int right = (int) (rect.left + areaRectTemp.right * width);
            int bottom = (int) (rect.top + areaRectTemp.bottom * height);
            areaRect = new Rect(left, top, right, bottom);
        }
    }


    public void reset() {
        if (srcData != null && srcData.length > 0) {
            for (BaseImageBean aSrcData : srcData) {
                aSrcData.isShow = true;
            }
            invalidate();
        }
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss(PullImageMoreView moveMoreImageView, int total, int dismissCount, int currentPos);
    }
}
