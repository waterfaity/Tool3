package com.waterfairy.tool3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.waterfairy.utils.PermissionUtils;
import com.waterfairy.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToastUtils.initToast(getApplicationContext());
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_STORAGE);
        PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_RECORD);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mp3:
                startActivity(new Intent(this, Mp3Activity.class));
                break;   case R.id.record:
                startActivity(new Intent(this, RecordActivity.class));
                break;
        }
    }
}
