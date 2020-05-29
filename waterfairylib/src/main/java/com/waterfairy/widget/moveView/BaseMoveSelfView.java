package com.waterfairy.widget.moveView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/9/3 14:31
 * @info:
 */
public class BaseMoveSelfView extends BaseView {
    private static final String TAG = "BaseMoveSelfView";
    protected BaseImageBean[] srcData;//拖动的图片
    protected BaseImageBean bgImageBean;//背景图片
    protected BaseImageBean currentImageBean;//当前移动的bean
    protected int currentPos = -1;//选中的位置
    protected boolean hasInit;//是否已经初始化
    protected float startY, startX;//移动上一个点
    protected boolean isAnimRunning;//动画中
    protected ValueAnimator resetValueAnimator;//复位动画
    private long touchDowntime;//按下时间


    public BaseMoveSelfView(Context context) {
        super(context);
    }

    public BaseMoveSelfView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImgRes(BaseImageBean imgRes[]) {
        destroySrcDataBitmap();
        this.srcData = imgRes;
        hasInit = false;
    }

    /**
     * 背景
     *
     * @param bgRes
     */
    public void setBgView(int bgRes) {
        destroyAnim();
        if (bgImageBean != null) {
            destroyBitmap(bgImageBean.bitmap);
        }
        bgImageBean = new BaseImageBean(0, 0, 1, 1, bgRes);
        hasInit = false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (bgImageBean != null && bgImageBean.bitmap != null && !bgImageBean.bitmap.isRecycled()) {
            //背景绘制
            canvas.drawBitmap(bgImageBean.bitmap, bgImageBean.rectFImg, bgImageBean.rectFSrc, null);
        }
        if (srcData != null) {
            //数据图片  移动的/未移动的 隐藏的不在绘制
            for (BaseImageBean srcDatum : srcData) {
                if (srcDatum.isMoving && srcDatum.isShow) {
                    canvas.drawBitmap(srcDatum.bitmap, srcDatum.rectFImg, srcDatum.rectFMove, null);
                } else if (srcDatum.isShow) {
                    canvas.drawBitmap(srcDatum.bitmap, srcDatum.rectFImg, srcDatum.rectFSrc, null);
                }
            }
        }
    }

    @Override
    public void onViewMeasure(boolean changed, int width, int height) {
        if (!hasInit) {
            onCalc();
            hasInit = true;
        }
    }

    protected void onCalc() {
        //计算背景
        if (bgImageBean != null) {
            destroyBitmap(bgImageBean.bitmap);
            bgImageBean.calcRectUseImg(getContext(), super.width, super.height);
        }
        //计算拖动图片
        if (srcData != null && srcData.length > 0) {
            for (BaseImageBean aSrcData : srcData) {
                aSrcData.calcRectUseImg(getContext(), super.width, super.height);
            }
        }
    }

    /**
     * 销毁动画
     */
    protected void destroyAnim() {
        if (resetValueAnimator != null) {
            resetValueAnimator.cancel();
            resetValueAnimator = null;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnimRunning) return true;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            onDown(event);
        } else if (action == MotionEvent.ACTION_MOVE) {
            onMove(event);
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            onUp(event);
        }
        return true;
    }

    /**
     * onEvent
     *
     * @param event
     */
    protected void onDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (srcData != null) {
            boolean isSelect = false;
            for (int i = 0; i < srcData.length; i++) {
                BaseImageBean srcDatum = srcData[i];
                if (srcDatum.isShow && srcDatum.canMove) {
                    //按下的点在 指定区域内 isSelect = true
                    if (x > srcDatum.rectFSrc.left && x < srcDatum.rectFSrc.right && y > srcDatum.rectFSrc.top && y < srcDatum.rectFSrc.bottom) {
                        if (currentImageBean != null) {
                            currentImageBean.isMoving = false;
                        }
                        currentImageBean = srcDatum;
                        currentImageBean.isMoving = true;
                        isSelect = true;
                        currentPos = i;
                    }
                }
                if (isSelect) {
                    startX = x;
                    startY = y;
                    onDownOption(currentImageBean, i);
                    break;
                } else {
                    //未选中  置空
                    touchDowntime = 0;
                    currentPos = -1;
                    currentImageBean = null;
                }
            }
        }
    }

    protected void onDownOption(BaseImageBean imageBean, int pos) {
        touchDowntime = System.currentTimeMillis();
    }

    /**
     * onMove
     *
     * @param event
     */
    protected void onMove(MotionEvent event) {
        if (currentImageBean != null && currentImageBean.isMoving && currentImageBean.isShow) {

            float dx = event.getX() - startX;
            float dy = event.getY() - startY;

            //计算 rectFMove 的坐标  在  rectFSrc的基础上 加差值

            currentImageBean.rectFMove.left = (int) (currentImageBean.rectFSrc.left + dx);
            currentImageBean.rectFMove.right = (int) (currentImageBean.rectFSrc.right + dx);
            currentImageBean.rectFMove.top = (int) (currentImageBean.rectFSrc.top + dy);
            currentImageBean.rectFMove.bottom = (int) (currentImageBean.rectFSrc.bottom + dy);

            invalidate();
        }
    }

    /**
     * onUP
     *
     * @param event
     */
    protected void onUp(MotionEvent event) {
        if (currentImageBean != null && currentImageBean.isMoving) {
            //用于监听点击事件
            if (onOptionClickListener != null) {
                long l = System.currentTimeMillis() - touchDowntime;
                float distance = currentImageBean.getDistance();
                Log.i(TAG, "onUp: " + l + "  " + distance);
                if (l < getClickDelayTime() && distance < getMoveDistanceLimit()) {
                    onOptionClickListener.onOptionClick(this, currentImageBean, currentPos);
                    return;
                }
            }
            //1.判断是否移动到指定区域的中心点
            //2.判断是否移动指定区域中
            //注:使用 1 判断

            if (currentImageBean.stopReset) {
                //拖动后复位
                startResetAnim();
            } else if (!handleUpAction(event)) {
                startResetAnim();
            }
        }
    }


    /**
     * 处理抬起事件
     *
     * @param event
     * @return 是否处理 / 返回FALSE  有复位动画
     */
    protected boolean handleUpAction(MotionEvent event) {
        return onTouchUpCallback != null && onTouchUpCallback.onTouchUp(this, currentImageBean, currentPos);
    }

    /**
     * 复位动画
     */
    protected void startResetAnim() {
        if (resetValueAnimator == null) {
            resetValueAnimator = new ValueAnimator();
            resetValueAnimator.setDuration(300);
            resetValueAnimator.setFloatValues(1F);
            resetValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float value = (float) animation.getAnimatedValue();
                    //所有isMoving=true的都处理
                    if (srcData != null) {
                        for (BaseImageBean srcDatum : srcData) {
                            if (srcDatum.isMoving) {
                                animMove(srcDatum, value);
                            }
                        }
                    }
                    //刷新
                    invalidate();
                    if (value >= 1F) {
                        onResetAnimEnd();
                    }
                }
            });
        }
        isAnimRunning = true;
        resetValueAnimator.start();

    }

    /**
     * moveRectCopy为恢复前的坐标
     * 根据动画的改变 而改变moveRect坐标
     *
     * @param srcDatum
     * @param value
     */
    private void animMove(BaseImageBean srcDatum, float value) {
        Rect moveRectCopy = srcDatum.getMoveRectCopy();
        int widthTemp = (int) ((moveRectCopy.left - srcDatum.rectFSrc.left) * value);
        int heightTemp = (int) ((moveRectCopy.top - srcDatum.rectFSrc.top) * value);

        srcDatum.rectFMove.left = moveRectCopy.left - widthTemp;
        srcDatum.rectFMove.top = moveRectCopy.top - heightTemp;
        srcDatum.rectFMove.right = moveRectCopy.right - widthTemp;
        srcDatum.rectFMove.bottom = moveRectCopy.bottom - heightTemp;
    }

    /**
     * 复位动画结束
     */
    protected void onResetAnimEnd() {
        isAnimRunning = false;
        if (srcData != null) {
            for (BaseImageBean imageBean : srcData) {
                if (imageBean.isMoving)
                    imageBean.resetMoveRect();
            }
        }
    }


    /**
     * 设置srcData时使用
     */
    private void destroySrcDataBitmap() {
        destroyAnim();
        if (srcData != null && srcData.length > 0) {
            for (BaseImageBean srcDatum : srcData) {
                if (srcDatum.bitmap != null && !srcDatum.bitmap.isRecycled()) {
                    srcDatum.bitmap.recycle();
                    srcDatum.bitmap = null;
                }
                srcDatum = null;
            }
        }
    }

    /**
     * 销毁调用
     */
    public void onDestroy() {
        //销毁动画
        destroyAnim();
        //销毁背景
        if (bgImageBean != null) {
            destroyBitmap(bgImageBean.bitmap);
            bgImageBean = null;
        }
        //销毁数据
        if (srcData != null && srcData.length > 0) {
            for (BaseImageBean aSrcData : srcData) {
                if (aSrcData != null) {
                    if (aSrcData.bitmap != null) {
                        if (!aSrcData.bitmap.isRecycled()) {
                            aSrcData.bitmap.recycle();
                        }
                        aSrcData.bitmap = null;
                    }
                    aSrcData = null;
                }
            }
        }
        srcData = null;
        currentImageBean = null;
        currentPos = -1;
    }

    private OnOptionClickListener onOptionClickListener;
    private OnTouchUpCallback onTouchUpCallback;

    public void setOnOptionClickListener(OnOptionClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
    }

    public interface OnOptionClickListener {
        void onOptionClick(BaseMoveSelfView sortView, BaseImageBean currentImageBean, int pos);
    }

    public void setOnTouchUpCallback(OnTouchUpCallback onTouchUpCallback) {
        this.onTouchUpCallback = onTouchUpCallback;
    }

    public interface OnTouchUpCallback {
        boolean onTouchUp(BaseMoveSelfView sortView, BaseImageBean currentImageBean, int pos);
    }

    protected int getClickDelayTime() {
        return 200;
    }

    protected int getMoveDistanceLimit() {
        return 10;
    }
}
