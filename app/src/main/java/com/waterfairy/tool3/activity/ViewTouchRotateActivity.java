package com.waterfairy.tool3.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.ToastUtils;
import com.waterfairy.utils.ViewTouchRotateTool;

public class ViewTouchRotateActivity extends AppCompatActivity {
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_touch_rotate);
        view = findViewById(R.id.view);
        new ViewTouchRotateTool(findViewById(R.id.view_touch)).setOnRotateListener(new ViewTouchRotateTool.OnRotateListener() {
            @Override
            public void onRotate(float rotateAngle, float dDangle) {
//                ToastUtils.show("角度:" + rotateAngle);
                view.setRotation(view.getRotation()+dDangle);
            }
        });
    }
}