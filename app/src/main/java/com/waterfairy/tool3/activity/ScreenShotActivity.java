package com.waterfairy.tool3.activity;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.ScreenShotUtils;
import com.waterfairy.utils.ToastUtils;

public class ScreenShotActivity extends AppCompatActivity implements View.OnClickListener, ScreenShotUtils.OnScreenShotListener {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);
        imageView = (ImageView) findViewById(R.id.img);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int fromY = ScreenShotUtils.getStatusBarHeight(this);
        View rootView = getWindow().getDecorView().getRootView();
//        rootView = imageView;
        ScreenShotUtils.shot(rootView, "/sdcard/jj/j.png", 0, fromY, rootView.getRight() - rootView.getLeft(), rootView.getBottom() - rootView.getTop(), this);
    }

    @Override
    public void onShotSuccess(String shotPath) {
        Log.i("screenShot", "onShotSuccess: ");
        imageView.setImageBitmap(BitmapFactory.decodeFile(shotPath));
    }

    @Override
    public void onShotError(String error) {
        Log.i("screenShot", "onShotError: ");
        ToastUtils.show(error);
    }
}
