package com.waterfairy.tool3.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.PermissionUtils;
import com.waterfairy.widget.flipView.FlipAdapter;
import com.waterfairy.widget.flipView.FlipView;
import com.waterfairy.widget.flipView.FlipViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FlipViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_view);
        FlipViewUtils.initCachePath(getExternalCacheDir() + "/flipView");

        if (PermissionUtils.requestPermission(this, PermissionUtils.REQUEST_STORAGE)) {
            load();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean b = PermissionUtils.onRequestPermissionsResultForSDCard(permissions, grantResults);
        if (b) {
            load();
        }
    }

    private void load() {
        final File file = new File("/sdcard/DCIM/Camera");
        final File[] files = file.listFiles();
        final List<File> files1 = new ArrayList<>();
        for (File file1 : files) {
            if (file1.getName().endsWith(".jpg")) {
                files1.add(file1);
            }
        }


        FlipView flipView = (FlipView) findViewById(R.id.flip_view);
        flipView.setAdapter(new FlipAdapter() {
            @Override
            public int getCount() {
                return files1.size();
            }

            @Override
            public Bitmap getBitmap(int position) {
                return FlipViewUtils.getBitmap(files1.get(position).getAbsolutePath(), 1024, 720);
            }
        });
    }
}
