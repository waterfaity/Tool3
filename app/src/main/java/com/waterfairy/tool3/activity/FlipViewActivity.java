package com.waterfairy.tool3.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.waterfairy.tool3.R;
import com.waterfairy.widget.flipView.FlipAdapter;
import com.waterfairy.widget.flipView.FlipView;

import java.io.File;

public class FlipViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_view);

        File file = new File("/DCIM/Camera");
        final File[] files = file.listFiles();

        FlipView flipView = (FlipView) findViewById(R.id.flip_view);
        flipView.setAdapter(new FlipAdapter() {
            @Override
            public int getCount() {
                if (files != null) {
                    return files.length;
                }
                return 0;
            }

            @Override
            public Bitmap getBitmap(int position) {

                return null;
            }
        });
    }
}
