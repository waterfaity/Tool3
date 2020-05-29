package com.waterfairy.tool3.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.ToastUtils;
import com.waterfairy.widget.chart.ringChart.RingChartEntity;
import com.waterfairy.widget.chart.ringChart.RingChartView;

import java.util.ArrayList;
import java.util.List;

public class RingChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_chart);
        final List<RingChartEntity> ringChartEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ringChartEntities.add(new Entity("hah" + i, 9));
        }
        RingChartView ringChartView = findViewById(R.id.ring_view);
        ringChartView.setData(ringChartEntities);
        ringChartView.selectPos(3);
        ringChartView.setOnPartSelectListener(new RingChartView.OnPartSelectListener() {
            @Override
            public void onPartSelect(int pos, RingChartEntity ringChartEntity) {
                ToastUtils.show(pos + "--");
            }
        });
    }

    private int getPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    public class Entity implements RingChartEntity {
        public Entity(String title, float value) {
            this.title = title;
            this.value = value;
        }

        private String title;
        private float value;

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public float getValue() {
            return value;
        }

        @Override
        public String getValueStr() {
            return null;
        }

        @Override
        public int getType() {
            return 0;
        }
    }
}