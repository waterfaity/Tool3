package com.waterfairy.widget.flipView;

import android.graphics.Bitmap;

/**
 * user : water_fairy
 * email:995637517@qq.com
 * date :2017/10/14
 * des  :
 */

public interface FlipAdapter {
    int getCount();

    Bitmap getBitmap(int position);

}
