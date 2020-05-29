package com.waterfairy.tool3.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.ImageUtils;

public class ImgToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_tool);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.view_geometric_protractor);
        Bitmap matrix = ImageUtils.matrix(bitmap, .6f, .6f);
//        Bitmap trans = ImageUtils.colorToTrans(bitmap, -1, 200);
        ImageUtils.saveBitmap("/sdcard/jjjj.png", matrix, Bitmap.CompressFormat.PNG, 100);
    }
}
