package com.waterfairy.widget.moveView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.waterfairy.utils.ImageUtils;
import com.waterfairy.widget.utils.RectUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/9/3 14:02
 * @info:
 */
public class BaseImageBean {


    public BaseImageBean(float startXRadio, float startYRadio, float widthRadio, float heightRadio, int imgRes) {
        this.startXRadio = startXRadio;
        this.startYRadio = startYRadio;
        this.widthRadio = widthRadio;
        this.heightRadio = heightRadio;
        this.imgRes = imgRes;
        applyHeightRadio = true;
    }

    public BaseImageBean(float startXRadio, float startYRadio, float widthRadio, int imgRes) {
        this.startXRadio = startXRadio;
        this.startYRadio = startYRadio;
        this.widthRadio = widthRadio;
        this.imgRes = imgRes;
        applyHeightRadio = false;
    }

    public BaseImageBean(float startXRadio, float startYRadio, float widthRadio, float radioWH) {
        this.startXRadio = startXRadio;
        this.startYRadio = startYRadio;
        this.widthRadio = widthRadio;
        this.radioWH = radioWH;
        applyHeightRadio = false;
    }

    public Object tag;

    public float radioWH;
    private static final String TAG = "baseImageBean";
    public float startXRadio, startYRadio;//起点 x,y  比例
    public float widthRadio;//宽占比
    public float heightRadio;//高占比
    public boolean applyHeightRadio;
    public int imgRes;//图片
    public String imgPath;//图片路径
    public Bitmap bitmap;//bitmap
    public boolean canMove = true;
    public boolean stopReset;//停止移动时是否复位
    public boolean isShow = true;//展示
    public boolean isMoving = false;//移动中
    public Rect rectFSrc;//原位置
    public Rect rectFSrcCopy;//原位置copy
    public Rect rectFMove;//移动后的位置
    public Rect rectFMoveCopy;//移动copy的位置
    public Rect rectFImg;//图片的rectF


    public void resetMoveRect() {
        isMoving = false;
        rectFMove.left = rectFSrc.left;
        rectFMove.right = rectFSrc.right;
        rectFMove.top = rectFSrc.top;
        rectFMove.bottom = rectFSrc.bottom;
        rectFMoveCopy = null;
    }

    public Rect getMoveRectCopy() {
        if (rectFMoveCopy == null) rectFMoveCopy = new Rect(rectFMove);
        return rectFMoveCopy;
    }

    /**
     * 计算
     *
     * @param context
     * @param viewWidth
     * @param viewHeight
     */
    public void calcRectUseImg(Context context, int viewWidth, int viewHeight) {
        if (imgRes != 0) {
            try {
                bitmap = ImageUtils.decodeFromRes(context, imgRes, viewWidth, viewHeight, false);
            } catch (IOException e) {
                e.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(), imgRes);
            }
        } else if (!TextUtils.isEmpty(imgPath)) {
            try {
                bitmap = ImageUtils.decodeFromFile(new File(imgPath), viewWidth, viewHeight, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bitmap == null) {
            Log.i(TAG, "calcRectUseImg: bitmap is null");
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        calcRect(viewWidth, viewHeight, width, height);
    }

    /**
     * 用于计算 以屏幕为参考的区域
     *
     * @param viewWidth
     * @param viewHeight
     */
    public void calcRect(int viewWidth, int viewHeight) {
        int width = 0, height = 0;
        if (widthRadio != 0) width = (int) (widthRadio * viewWidth);
        if (heightRadio != 0) height = (int) (heightRadio * viewHeight);
        if (radioWH != 0 && width != 0) height = (int) (width / radioWH);
        calcRect(viewWidth, viewHeight, width, height);
    }

    /**
     * @param viewWidth
     * @param viewHeight
     * @param width
     * @param height
     */
    private void calcRect(int viewWidth, int viewHeight, int width, int height) {

        int left = (int) (startXRadio * viewWidth);
        int top = (int) (startYRadio * viewHeight);


        int right = (int) (left + widthRadio * viewWidth);
        int bottom = applyHeightRadio ? ((int) (top + heightRadio * viewHeight)) :
                ((int) (top + widthRadio * viewWidth * height / width));

        if (width == 0 || height == 0) {
            rectFImg = new Rect(0, 0, right - left, bottom - top);
        } else {
            rectFImg = new Rect(0, 0, width, height);
        }
        rectFSrc = RectUtils.getCenterRect(new RectF(rectFImg), new RectF(left, top, right, bottom), true);

        rectFSrcCopy = new Rect(rectFSrc);
        rectFMove = new Rect(rectFSrc);
    }


    public BaseImageBean setCanMove(boolean canMove) {
        this.canMove = canMove;
        return this;
    }

    public BaseImageBean setShow(boolean show) {
        this.isShow = show;
        return this;
    }


    public BaseImageBean setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public float getDistance() {
        if (rectFSrcCopy != null) {
            double v = 0;
            if (rectFMoveCopy != null)
                v = Math.pow(rectFSrcCopy.centerX() - rectFMoveCopy.centerX(), 2) + Math.pow(rectFSrcCopy.centerY() - rectFMoveCopy.centerY(), 2);
            else if (rectFMove != null) {
                v = Math.pow(rectFSrcCopy.centerX() - rectFMove.centerX(), 2) + Math.pow(rectFSrcCopy.centerY() - rectFMove.centerY(), 2);
            }
            return (float) Math.sqrt(v);
        }
        return 0;
    }

    public BaseImageBean setStopReset(boolean stopReset) {
        this.stopReset = stopReset;
        return this;
    }
}

