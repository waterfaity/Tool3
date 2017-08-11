package com.waterfairy.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * glide获取图片
 * Created by water_fairy on 2017/6/6.
 * 995637517@qq.com
 */

public class CacheImgTransformation extends BitmapTransformation {
    private OnGetBitmapListener onGetBitmapListener;

    public CacheImgTransformation(Context context, OnGetBitmapListener onGetBitmapListener) {
        super(context);
        this.onGetBitmapListener = onGetBitmapListener;
    }

    @Override
    public Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
        if (onGetBitmapListener != null) onGetBitmapListener.onGetBitmap(bitmap);
        return bitmap;
    }

    @Override
    public String getId() {
        return "cache_img";
    }

    public interface OnGetBitmapListener {
        void onGetBitmap(Bitmap bitmap);
    }
}
