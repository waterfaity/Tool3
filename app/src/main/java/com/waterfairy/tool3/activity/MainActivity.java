package com.waterfairy.tool3.activity;

import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;

import com.waterfairy.tool3.R;
import com.waterfairy.tool3.recyclerview.RecyclerViewActivity;
import com.waterfairy.utils.PermissionUtils;
import com.waterfairy.utils.RingTonUtils;
import com.waterfairy.utils.ToastUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastUtils.initToast(getApplicationContext());
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_STORAGE);
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_RECORD);

    }

    /**
     * 1.算法名字
     * 2.内容
     */
    private void test() {
        RingTonUtils.PlayRingTone(this, RingtoneManager.TYPE_RINGTONE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test:
                startActivity(new Intent(this, TestActivity.class));
                break;
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
            case R.id.flip_view_2:
                startActivity(new Intent(this, FlipView2Activity.class));
                break;
            case R.id.ring_chart_view:
                startActivity(new Intent(this, RingChartActivity.class));
                break;
            case R.id.wx_img:
                startActivity(new Intent(this, WXCacheImgActivity.class));
                break;
            case R.id.location:
                startActivity(new Intent(this, LocationActivity.class));
                break;
            case R.id.matrix:
                startActivity(new Intent(this, MatrixActivity.class));
                break;
            case R.id.bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.view_touch_rotate:
                startActivity(new Intent(this, ViewTouchRotateActivity.class));
                break;
        }
    }
}
