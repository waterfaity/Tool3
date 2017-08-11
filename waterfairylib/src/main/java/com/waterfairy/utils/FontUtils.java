package com.waterfairy.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

/**
 * Created by water_fairy on 2017/5/16.
 * 995637517@qq.com
 */

public class FontUtils {
    public static void setFontType(Context context, TextView textView, String fontPath) {
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), fontPath);
        textView.setTypeface(typeFace);
    }
}
