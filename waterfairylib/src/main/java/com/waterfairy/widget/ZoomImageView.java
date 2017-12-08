package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseSelfView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/12/8
 * @Description:
 */

public class ZoomImageView extends BaseSelfView {
    private static final String TAG = "ZoomImageView";
    private Bitmap bitmap;
    private boolean init;
    private float zoom = 1;
    private Rect rectFSource;
    private Rect rectFTarget;
    private int widthBitmap, heightBitmap;
    private boolean isMove;

    private float x1, y1, x2, y2;
    private float startDistance;//初始距离

    public ZoomImageView(Context context) {
        super(context);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void beforeDraw() {
        super.beforeDraw();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {
            //单指
        } else {
            //双指
        }
        int action = event.getAction();
        if (action != MotionEvent.ACTION_MOVE) {
            Log.i(TAG, "onTouchEvent: " + event.getAction());
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                Log.i(TAG, "onTouchEvent:ACTION_DOWN\t" + pointerCount);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent:ACTION_UP\t" + pointerCount);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent:ACTION_MOVE\t" + pointerCount);
                handleMove(event, pointerCount);
                break;
            case MotionEvent.ACTION_POINTER_1_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                startDistance = (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
                Log.i(TAG, "第1个点按下\t" + pointerCount + "--" + startDistance);
                break;
            case MotionEvent.ACTION_POINTER_1_UP:
                Log.i(TAG, "第1个点抬起\t" + pointerCount);
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                Log.i(TAG, "第2个点按下\t" + pointerCount+ "--" + startDistance);
                x2 = event.getX(1);
                y2 = event.getY(1);
                startDistance = (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
                Log.i(TAG, "第2个点抬起\t" + pointerCount);
                break;
            case MotionEvent.ACTION_CANCEL:
                //取消
                Log.i(TAG, "触摸取消\t" + pointerCount);
                break;
        }
        return true;
    }

    private void handleMove(MotionEvent event, int pointerCount) {
        if (pointerCount == 1) {
            move(event);
        } else if (pointerCount == 2) {
            zoom(event);
        }
    }

    private void move(MotionEvent event) {

    }

    private void zoom(MotionEvent event) {
        float currentX1 = event.getX(0);
        float currentY1 = event.getY(0);
        float currentX2 = event.getX(1);
        float currentY2 = event.getY(1);

        float currentDistance = (float) Math.sqrt(Math.pow((currentX2 - currentX1), 2) + Math.pow((currentY2 - currentY1), 2));
        Log.i(TAG, "zoom: " + startDistance + "--" + currentDistance);

    }

    @Override
    protected void drawOne(Canvas canvas) {
        canvas.drawBitmap(bitmap, getSourceRect(), getTargetRect(), null);
    }

    private Rect getSourceRect() {
        if (zoom == 1) {
            rectFSource = new Rect(0, 0, widthBitmap, heightBitmap);
        } else if (isMove) {

        } else {

        }
        return rectFSource;
    }

    public Rect getTargetRect() {
        return rectFSource;
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            this.bitmap = bitmap;
            widthBitmap = bitmap.getWidth();
            heightBitmap = bitmap.getHeight();
            zoom = 1;
            onInitDataOk();
        }
    }

    public void setImgFile(String imgFile) {
        setBitmap(BitmapFactory.decodeFile(imgFile));
    }

    public void setImgRes(int imgRes) {
        setBitmap(BitmapFactory.decodeResource(getResources(), imgRes));
    }

}
