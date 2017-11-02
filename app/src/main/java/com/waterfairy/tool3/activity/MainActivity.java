package com.waterfairy.tool3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.waterfairy.tool3.R;
import com.waterfairy.tool3.recyclerview.RecyclerViewActivity;
import com.waterfairy.utils.MathTools;
import com.waterfairy.utils.PermissionUtils;
import com.waterfairy.utils.ToastUtils;
import com.waterfairy.widget.baseView.Coordinate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastUtils.initToast(getApplicationContext());
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_STORAGE);
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_RECORD);
        test();
    }

    private void test() {
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 0);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 30);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 45);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 60);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 90);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 120);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 135);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 180);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 225);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 315);
        MathTools.transPoint2(new Coordinate(200, 0), new Coordinate(0, 0), 360);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mp3:
                startActivity(new Intent(this, Mp3Activity.class));
                break;
            case R.id.record:
                startActivity(new Intent(this, RecordActivity.class));
                break;
            case R.id.net:
                startActivity(new Intent(this, NetStateActivity.class));
                break;
            case R.id.recycler:
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
            case R.id.screen_shot:
                startActivity(new Intent(this, ScreenShotActivity.class));
                break;
            case R.id.img_tool:
                startActivity(new Intent(this, ImgToolActivity.class));
                break;
        }
    }
}
