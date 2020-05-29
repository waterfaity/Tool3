package com.waterfairy.tool3.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.waterfairy.imageselect.widget.ZoomImageView;
import com.waterfairy.tool3.R;
import com.waterfairy.utils.ToastUtils;
import com.waterfairy.widget.chart.ringChart.RingChartEntity;
import com.waterfairy.widget.chart.ringChart.RingChartView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Glide.with(this).load(R.mipmap.ic_launcher).into((ImageView) findViewById(R.id.zoom_img));

        ((ZoomImageView) findViewById(R.id.zoom_img)).setCanZoom(true);

    }

}
