package com.waterfairy.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by water_fairy on 2017/7/26.
 * 995637517@qq.com
 */

public class AlphaBgImgCreator {
    private int mWidth, mHeight, mCellWith;

    public AlphaBgImgCreator(int mWidth, int mHeight, int mCellWith) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mCellWith = mCellWith;
    }

    public Bitmap createBg() {
        Bitmap bitmapCell = createBgCell();
        Bitmap repeatHor = createRepeatHor(mWidth, bitmapCell);
        return createRepeatVer(mHeight, repeatHor);
    }

    public Bitmap createCircleBg() {
        return ImageUtils.round(createBg(), mWidth / 2);
    }

    private Bitmap createBgCell() {

        Bitmap alphaBgBitmap = Bitmap.createBitmap(mCellWith * 2, mCellWith * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(alphaBgBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, mCellWith, mCellWith, paint);
        canvas.drawRect(mCellWith, mCellWith, 2 * mCellWith, 2 * mCellWith, paint);
        paint.setColor(Color.GRAY);
        canvas.drawRect(mCellWith, 0, 2 * mCellWith, mCellWith, paint);
        canvas.drawRect(0, mCellWith, mCellWith, 2 * mCellWith, paint);
        return alphaBgBitmap;
    }

    private Bitmap createRepeatHor(int width, Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        count = (width - 1) / src.getWidth() + 1;
        for (int i = 0; i < count; i++) {
            canvas.drawBitmap(src, i * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    private Bitmap createRepeatVer(int height, Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        count = (height - 1) / src.getHeight() + 1;
        for (int i = 0; i < count; i++) {
            canvas.drawBitmap(src, 0, i * src.getHeight(), null);
        }
        return bitmap;
    }
}
