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
            "#faca02", "#dca932", "#f16f02", "#ffe8d0", "#ffa6a1", "#ec3907",
            "#b93800", "#d43265", "#955740", "#633630", "#25211c", "#a0a0a0",
            "#ffffff", "#5a1973", "#7d41a5", "#340f6f", "#03549f", "#0088e2",
            "#7cdcff", "#38b38c", "#017e43", "#00b03f", "#6dc134", "#fbfb00",
    };
    private float anglePer = 360F / colors.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_touch_rotate);
        view = findViewById(R.id.view);

        findViewById(R.id.color_view).setBackgroundColor(Color.parseColor(colors[colors.length - 1]));
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
        rotateAngle = (rotateAngle + (anglePer / 2)) % 360;

        int index = (int) (rotateAngle / anglePer);
        Log.i("TAG", "calcColor: " + index + " " + rotateAngle);
        if (lastIndex != index) {
            String color = colors[colors.length - 1 - index];
            findViewById(R.id.color_view).setBackgroundColor(Color.parseColor(color));
            Log.i("TAG", "rotateAngle:" + rotateAngle + "\tcalcColor: " + index + " \tanglePer:" + anglePer);
            lastIndex = index;
        }
    }
}