package com.waterfairy.tool3.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.ViewTouchRotateTool;

public class ViewTouchRotateActivity extends AppCompatActivity {
    private View view;

    private String[] colors = {
            "#faca02", "#dca932", "#f16f02", "#ffe8d0", "#ffa6a1",
            "#ee3a06", "#b93800", "#d43265", "#975941", "#633630",
            "#25211c", "#a0a0a0", "#ffffff", "#5a1973", "#7a3fa1",
            "#faca02", "#330f6c", "#05539e", "#047fd3", "#7ad7f9",
            "#38b38c", "#017e43", "#00b03f", "#6dc134", "#fbfb00",
    };
    private float anglePer = 360 / 25F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_touch_rotate);
        view = findViewById(R.id.view);

        findViewById(R.id.color_view).setBackgroundColor(Color.parseColor(colors[24]));
        new ViewTouchRotateTool(findViewById(R.id.view_touch)).setOnRotateListener(new ViewTouchRotateTool.OnRotateListener() {
            @Override
            public void onRotate(float rotateAngle, float dDangle) {
                view.setRotation(rotateAngle);
                calcColor(rotateAngle);
            }
        });
    }

    int lastIndex = 0;

    private void calcColor(float rotateAngle) {

        int index = (int) (rotateAngle / anglePer);
        Log.i("TAG", "calcColor: " + index + " " + rotateAngle);
        if (lastIndex != index) {
            String color = colors[24 - index];
            findViewById(R.id.color_view).setBackgroundColor(Color.parseColor(color));
            Log.i("TAG", "rotateAngle:" + rotateAngle + "\tcalcColor: " + index + " \tanglePer:" + anglePer);
            lastIndex = index;
        }
    }
}