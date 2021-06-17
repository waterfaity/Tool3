package com.waterfairy.utils;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2021-06-17 15:57
 * @info:
 */
public class GestureFlingRotateTool {
    private static final String TAG = "GestureFlingRo";


    //中心xy
    private float cx, cy;

    private OnFlingListener onFlingListener;

    private MyValueAnimator valueAnimator;

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.onFlingListener = onFlingListener;
    }

    /**
     * @param endEvent  终点
     * @param cx
     * @param cy
     * @param velocityX x速度(per second)
     * @param velocityY y速度
     */
    public void startFling(final MotionEvent endEvent, float cx, float cy, final float velocityX, final float velocityY) {
        this.cx = cx;
        this.cy = cy;
        stop();
        valueAnimator = new MyValueAnimator(endEvent, velocityX, velocityY, this.cx, this.cy);
        valueAnimator.setOnFlingListener(onFlingListener);
        valueAnimator.start();
    }


    public void stop() {
        if (valueAnimator != null) {
            valueAnimator.work = false;
            valueAnimator.setOnFlingListener(null);
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }


    public static class MyValueAnimator extends ValueAnimator {
        private final float cx, cy;
        public boolean work;//是否在工作

        private final MotionEvent endEvent;
        private final float velocityRotate;//起始旋转速度

        private OnFlingListener onFlingListener;
        private long currentTime;

        MyValueAnimator setOnFlingListener(OnFlingListener onFlingListener) {
            this.onFlingListener = onFlingListener;
            return this;
        }

        MyValueAnimator(MotionEvent endEvent, float velocityX, float velocityY, float cx, float cy) {

            this.endEvent = endEvent;
            this.cx = cx;
            this.cy = cy;
            this.velocityRotate = calcRotateVelocity(velocityX, velocityY);

            currentTime = System.currentTimeMillis();
            initListener();
            initAnim();
        }

        private float calcRotateVelocity(float velocityX, float velocityY) {

            Log.i(TAG, "------------------------------------------------------------------------");

            float dy = endEvent.getY() - cy;
            float dx = endEvent.getX() - cx;
            float m = (float) Math.atan(dy / dx);
            float n = (float) (Math.PI / 2 - m);
            int o = 1;
            if (endEvent.getY() > cy) {
                if (endEvent.getX() > cx) {
                    o = 1;
                } else {
                    o = 2;
                }
            } else {
                if (endEvent.getX() > cx) {
                    o = 4;
                } else {
                    o = 3;
                }
            }
            float dVx = (float) (velocityY * Math.cos(m));
            float dVy = (float) (velocityX * Math.cos(n));
            return ((o == 1 || o == 4 ? 1 : -1) * dVx + (o == 1 || o == 4 ? -1 : 1) * dVy);
        }

        private void initAnim() {
            setInterpolator(new AccelerateDecelerateInterpolator());
            setFloatValues(1, 0);
            setDuration(300);
        }

        private void initListener() {
            addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!work) return;
                    float radio = (float) animation.getAnimatedValue();
                    //时间差
                    long tempTime = System.currentTimeMillis();
                    long dTime = tempTime - currentTime;
                    currentTime = tempTime;

                    //位移=当前速度*时间差
                    float dAngle = velocityRotate * radio * (dTime / 1000F) / 2;
                    if (onFlingListener != null)
                        onFlingListener.onFling(dAngle);
                }
            });

            addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!work) return;
                    if (onFlingListener != null) onFlingListener.onFlingEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (!work) return;
                    if (onFlingListener != null) onFlingListener.onFlingEnd();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }

        @Override
        public void start() {
            work = true;
            super.start();
        }
    }


    public interface OnFlingListener {
        /**
         * @param dAngle 差异角度
         */
        void onFling(float dAngle);

        void onFlingEnd();
    }
}