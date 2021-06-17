package com.waterfairy.utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ViewTouchRotateTool {

    private static final String TAG = "ViewTouchRotate";
    private View view;
    private boolean enable;
    private GestureDetector gestureDetector;
    private OnRotateListener onRotateListener;
    GestureFlingRotateTool gestureFlingRotateTool;
    private int cx, cy;

    public ViewTouchRotateTool setOnRotateListener(OnRotateListener onRotateListener) {
        this.onRotateListener = onRotateListener;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ViewTouchRotateTool(View view) {
        this.view = view;
        init();
    }

    private void init() {
        this.view.post(new Runnable() {
            @Override
            public void run() {
                cx = view.getWidth() / 2;
                cy = view.getHeight() / 2;
            }
        });
        if (gestureFlingRotateTool == null) {
            gestureFlingRotateTool = new GestureFlingRotateTool();
            gestureFlingRotateTool.setOnFlingListener(generateFlingListener());
        }
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(view.getContext(), generateListener());
        }
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    /**
     * 手势监听
     *
     * @return
     */
    private GestureDetector.OnGestureListener generateListener() {
        return new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.i(TAG, "onScroll: ");
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                Log.i(TAG, "onLongPress: ");

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent endEvent, float vx, float vy) {
                Log.i(TAG, "onFling: ");
                gestureFlingRotateTool.startFling(endEvent, cx, cy, vx, vy);
                return false;
            }
        };
    }


    private GestureFlingRotateTool.OnFlingListener generateFlingListener() {
        return new GestureFlingRotateTool.OnFlingListener() {
            @Override
            public void onFling(float angle, float dAngle) {
                Log.i(TAG, "onFling: " + angle);
                if (onRotateListener != null) onRotateListener.onRotate(angle, dAngle);
            }

            @Override
            public void onFlingEnd() {

            }
        };
    }


    private void calcRotate(int touchX, int touchY) {
        int dx = touchX - cx;
        int dy = touchY - cy;

    }


    private void rotate(int rotate) {
        view.setRotation(rotate);
    }

    /**
     * 旋转监听
     */
    public interface OnRotateListener {
        //旋转角度
        void onRotate(float angle, float dAngle);
    }
}
