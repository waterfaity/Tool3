package com.waterfairy.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * glide获取图片
 * Created by water_fairy on 2017/6/6.
 * 995637517@qq.com
 */

public class CacheImgTransformation extends BitmapTransformation {
    private Object object;
    private OnGetBitmapListener onGetBitmapListener;
    private OnGetBitmap2Listener onGetBitmap2Listener;
    private OnGetBitmapWithSavePathListener onGetBitmapWithSavePathListener;
    private String saveImgPath;

    public CacheImgTransformation(Context context, OnGetBitmapListener onGetBitmapListener) {
        super(context);
        this.onGetBitmapListener = onGetBitmapListener;
    }

    public CacheImgTransformation(Context context, Object object, String saveImgPath, OnGetBitmap2Listener onGetBitmapListener) {
        super(context);
        this.object = object;
        this.saveImgPath = saveImgPath;
        this.onGetBitmap2Listener = onGetBitmapListener;
    }

    public CacheImgTransformation(Context context, String saveImgPath, OnGetBitmapWithSavePathListener onGetBitmapListener) {
        super(context);
        this.onGetBitmapWithSavePathListener = onGetBitmapListener;
        this.saveImgPath = saveImgPath;
    }

    @Override
    public Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
        if (onGetBitmapListener != null) onGetBitmapListener.onGetBitmap(bitmap);
        if (onGetBitmapWithSavePathListener != null)
            onGetBitmapWithSavePathListener.onGetBitmap(saveImgPath, bitmap);
        if (onGetBitmap2Listener != null) onGetBitmap2Listener.onGetBitmap(object,saveImgPath, bitmap);
        return bitmap;
    }

    @Override
    public String getId() {
        if (TextUtils.isEmpty(saveImgPath))
            return "cache_img";
        else return saveImgPath;
    }

    public interface OnGetBitmapListener {
        void onGetBitmap(Bitmap bitmap);
    }

    public interface OnGetBitmap2Listener {
        void onGetBitmap(Object object, String saveImgPath, Bitmap bitmap);
    }

    public interface OnGetBitmapWithSavePathListener {
        void onGetBitmap(String saveImgPath, Bitmap bitmap);
    }
}
