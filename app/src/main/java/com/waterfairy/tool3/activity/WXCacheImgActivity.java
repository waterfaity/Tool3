package com.waterfairy.tool3.activity;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.waterfairy.tool3.R;
import com.waterfairy.utils.FileUtils;
import com.waterfairy.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WXCacheImgActivity extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<File> imgFiles = new ArrayList<>();
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxcache_img);

        gridView = findViewById(R.id.grid_view);
        gridView.setNumColumns(4);
        gridView.setAdapter(adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return imgFiles.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(WXCacheImgActivity.this).inflate(R.layout.item_img, parent, false);
                }
                Glide.with(WXCacheImgActivity.this).load(imgFiles.get(position)).into((ImageView) convertView);
                return convertView;
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File file = imgFiles.get(position);
                try {
                    FileUtils.copyFile(file.getAbsolutePath(),"/sdcard/WeixinImg/"+file.getName()+".jpg");
                    ToastUtils.show("保存ok");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        query();
    }

    private void query() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                search(this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ToastUtils.show("完成!");
            }

            private void search(AsyncTask<Void, Void, Void> asyncTask) {
                String path = "/sdcard/Tencent/MicroMsg";
                List<File> searchPaths = new ArrayList<>();

                File file = new File(path);
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file1 = files[i];
                    if (file1.isDirectory() && file1.getName().length() > 30) {
                        searchPaths.add(new File(file1,"image2"));
                    }
                }

                for (int i = 0; i < searchPaths.size(); i++) {
                    search(searchPaths.get(i), asyncTask);
                }

            }

            private void search(File file, AsyncTask<Void, Void, Void> asyncTask) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            search(files[i], asyncTask);
                        } else {
                            imgFiles.add(files[i]);
                            publishProgress();
                        }
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }


}
