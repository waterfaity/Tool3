package com.waterfairy.tool3.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.ImageUtils;
import com.waterfairy.widget.flipView3.flip.FlipViewController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FlipView2Activity extends AppCompatActivity {
    private static final String TAG = "flipView2";
    private FlipViewController flipViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_view2);
        initView();
        initData();
    }

    private void initView() {
        flipViewController = findViewById(R.id.flip_view);
        flipViewController.setOnViewFlipListener(new FlipViewController.ViewFlipListener() {
            @Override
            public void onViewFlipped(View view, int position) {
                Log.i(TAG, "onViewFlipped: " + position);
                if (position == 1) {
                    flipViewController.setLimit(0, 500);
                } else {
                    flipViewController.setLimit(0, 0);
                }
            }
        });
    }

    private void initData() {
        final File file = new File("/sdcard/DCIM/Camera");
        final File[] files2 = file.listFiles();
        files = new ArrayList<>();
        for (File file1 : files2) {
            if (file1.getName().endsWith(".jpg")) {
                files.add(file1);
            }
        }
        flipViewController.setAdapter(adapter);
    }

    private List<File> files;
    private BaseAdapter adapter = new BaseAdapter() {


        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView: " + position);
            if (convertView == null) {
                convertView = new RelativeLayout(FlipView2Activity.this);
                RelativeLayout relativeLayout = (RelativeLayout) convertView;
                relativeLayout.addView(new ImageView(FlipView2Activity.this));
                relativeLayout.addView(new ListView(FlipView2Activity.this));
            }

            RelativeLayout relativeLayout = (RelativeLayout) convertView;
            ImageView imageView = (ImageView) relativeLayout.getChildAt(0);
            ListView listView = (ListView) relativeLayout.getChildAt(1);
            Bitmap bitmap = BitmapFactory.decodeFile(files.get(position).getAbsolutePath());
            Bitmap matrix = ImageUtils.matrix(bitmap, 1080, 1920, false);
            Log.i(TAG, "getView: " + matrix.getWidth());
            imageView.setImageBitmap(matrix);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list.add("hhhh" + i);
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(FlipView2Activity.this, android.R.layout.activity_list_item, android.R.id.text1, list);
            listView.setAdapter(arrayAdapter);
            return convertView;
        }
    };
}
