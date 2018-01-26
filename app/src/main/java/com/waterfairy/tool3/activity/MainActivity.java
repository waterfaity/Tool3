package com.waterfairy.tool3.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.waterfairy.tool3.MD5Utils;
import com.waterfairy.tool3.R;
import com.waterfairy.tool3.recyclerview.RecyclerViewActivity;
import com.waterfairy.utils.PathUtils;
import com.waterfairy.utils.PermissionUtils;
import com.waterfairy.utils.ToastUtils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastUtils.initToast(getApplicationContext());
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_STORAGE);
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_RECORD);
        test();
    }

    /**
     * 1.算法名字
     * 2.内容
     */
    private void test() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("hhhh");
        builder.setContentText("fdsa");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification build = builder.build();
        notificationManager.notify(890, build);

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
