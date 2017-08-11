package com.waterfairy.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.waterfairy.utils.ImageUtils;

/**
 * 取半
 * Created by water_fairy on 2017/6/6.
 * 995637517@qq.com
 */

public class HalfTransformation extends BitmapTransformation {
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final int BOTH = 2;
    private int dir;
    private Context mContext;
    private int mErrorImg;
    private String imgPath;
    private int width, height;
    private OnGetHalfImgListener onGetHalfImgListener;

    public HalfTransformation(Context context, String imgPath, int dir) {
        this(context, imgPath, dir, 0, 0, 0);
    }

    public HalfTransformation(Context context, String imgPath, int dir, int width, int height) {
        this(context, imgPath, dir, 0, width, height);
    }

    public HalfTransformation(Context context, String imgPath, int dir, int errorRes, int width, int height) {
        this(context, imgPath, dir, errorRes, null, width, height);
    }

    public HalfTransformation(Context context, String imgPath, int dir, int errorRes, OnGetHalfImgListener onGetHalfImgListener, int width, int height) {
        super(context);
        this.mContext = context;
        this.dir = dir;
        this.mErrorImg = errorRes;
        this.imgPath = imgPath;
        this.width = width;
        this.height = height;
        this.onGetHalfImgListener = onGetHalfImgListener;
    }

    public HalfTransformation(BitmapPool bitmapPool) {
        super(bitmapPool);
    }

    @Override
    protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int width, int height) {
        bitmap = ImageUtils.matrix(bitmap, this.width, this.height, true);//缩小
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int startX = 0, halfWidth = width - width / 2;
        Bitmap bitmapTemp = null;
        if (dir == BOTH) {
            Bitmap leftBitmap = null, rightBitmap = null;
            leftBitmap = Bitmap.createBitmap(bitmap, 0, 0, width / 2, height);
            rightBitmap = Bitmap.createBitmap(bitmap, halfWidth, 0, width / 2, height);
            leftBitmap = isNull(leftBitmap);
            rightBitmap = isNull(rightBitmap);
            if (onGetHalfImgListener != null)
                onGetHalfImgListener.onGetHalfImg(dir, leftBitmap, rightBitmap);
        } else {
            if (dir == LEFT) {
                startX = 0;
            } else if (dir == RIGHT) {
                startX = halfWidth;
            }
            bitmapTemp = Bitmap.createBitmap(bitmap, startX, 0, halfWidth, height);//一半
            bitmapTemp = isNull(bitmapTemp);
            if (onGetHalfImgListener != null)
                onGetHalfImgListener.onGetHalfImg(dir, bitmapTemp, null);
        }
        return bitmapTemp;
    }

    private Bitmap isNull(Bitmap bitmap) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), mErrorImg);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565);
            }
        }
        return bitmap;
    }

    @Override
    public String getId() {
        return imgPath + (dir == LEFT ? 0 : 1);
    }

    interface OnGetHalfImgListener {
        void onGetHalfImg(int dir, Bitmap bitmap1, Bitmap bitmap2);
    }
}
