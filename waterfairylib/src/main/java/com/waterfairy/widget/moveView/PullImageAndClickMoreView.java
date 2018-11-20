package com.waterfairy.widget.moveView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/8/30 16:43
 * @info: 拖动 或点击 消失在指定位置
 */
public class PullImageAndClickMoreView extends BaseMoveSelfView {
    private static final String TAG = "PullImageMoreView";

    private RectF areaRectTemp;
    private Rect dismissAreaRect;//消失区域
    private ValueAnimator dismissValueAnimator;
    private boolean showDismissPoint;

    public PullImageAndClickMoreView(Context context) {
        super(context);
    }

    public PullImageAndClickMoreView(Context context, AttributeSet attrs) {
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
                    Log.i(TAG, "onAnimationUpdate: " + value);
                    if (currentImageBean != null) {
                        Rect moveRectCopy = currentImageBean.getMoveRectCopy();
                        int widthTemp = (int) (moveRectCopy.width() * value);
                        int heightTemp = (int) (moveRectCopy.height() * value);

                        currentImageBean.rectFMove.left = moveRectCopy.left + widthTemp;
                        currentImageBean.rectFMove.top = moveRectCopy.top + heightTemp;
                        currentImageBean.rectFMove.right = moveRectCopy.right - widthTemp;
                        currentImageBean.rectFMove.bottom = moveRectCopy.bottom - heightTemp;

                        invalidate();

                    }
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
        if (dismissAreaRect != null && showDismissPoint) {
            canvas.drawCircle(dismissAreaRect.centerX(), dismissAreaRect.centerY(), 10, new Paint());
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
        if (dismissAreaRect != null) {
            int targetCenterX = dismissAreaRect.centerX();
            int targetCenterY = dismissAreaRect.centerY();
            int srcCenterX = currentImageBean.rectFSrc.centerX();
            int srcCenterY = currentImageBean.rectFSrc.centerY();
            int dx = targetCenterX - srcCenterX;
            int dy = targetCenterY - srcCenterY;

            currentImageBean.rectFSrc.left += dx;
            currentImageBean.rectFSrc.right += dx;
            currentImageBean.rectFSrc.top += dy;
            currentImageBean.rectFSrc.bottom += dy;
        }
        return false;
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

    @Override
    protected void onResetAnimEnd() {
        super.onResetAnimEnd();
        if (currentImageBean != null)
            currentImageBean.isMoving = true;
        if (!isReset) {
            startDismissAnim();
        } else {
            isReset = false;
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
            dismissAreaRect = new Rect(left, top, right, bottom);
        }
    }

    boolean isReset = false;

    public void reset() {
        if (srcData != null && srcData.length > 0) {
            for (BaseImageBean aSrcData : srcData) {
                aSrcData.isShow = true;
                if (aSrcData.rectFMoveCopy == null) aSrcData.rectFMoveCopy = new Rect();
                aSrcData.rectFMoveCopy.left = aSrcData.rectFSrc.left;
                aSrcData.rectFMoveCopy.right = aSrcData.rectFSrc.right;
                aSrcData.rectFMoveCopy.bottom = aSrcData.rectFSrc.bottom;
                aSrcData.rectFMoveCopy.top = aSrcData.rectFSrc.top;

                aSrcData.rectFSrc.left = aSrcData.rectFSrcCopy.left;
                aSrcData.rectFSrc.right = aSrcData.rectFSrcCopy.right;
                aSrcData.rectFSrc.bottom = aSrcData.rectFSrcCopy.bottom;
                aSrcData.rectFSrc.top = aSrcData.rectFSrcCopy.top;

                aSrcData.isMoving = true;
            }
            isReset = true;
            startResetAnim();
        }
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss(PullImageAndClickMoreView moveMoreImageView, int total, int dismissCount, int dismissPos);
    }
}
