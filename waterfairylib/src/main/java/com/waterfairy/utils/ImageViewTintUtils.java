package com.waterfairy.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/1 10:43
 * @info:
 */
public class ImageViewTintUtils {
    public static ImageView setTint(ImageView imageView, int tintColor, int imgRes) {
        if (imageView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageResource(imgRes);
                imageView.getDrawable().setTint(tintColor);
            } else {
                Drawable drawable = imageView.getContext().getResources().getDrawable(imgRes);
                Drawable tintIcon = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(tintIcon, tintColor);
                imageView.setImageDrawable(tintIcon);
            }
        }
        return imageView;
    }
}
