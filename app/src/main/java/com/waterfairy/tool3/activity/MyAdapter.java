package com.waterfairy.tool3.activity;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.waterfairy.tool3.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/16
 * @Description:
 */


public class MyAdapter extends RecyclerView.Adapter {
    private Context context;

    public MyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item2, parent, false)) {

        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
