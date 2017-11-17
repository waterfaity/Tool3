package com.waterfairy.tool3.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waterfairy.tool3.R;
import com.waterfairy.widget.recyclerview.expand.ExpandBaseAdapter;
import com.waterfairy.widget.recyclerview.expand.ExpandBean;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/16
 * @Description:
 */


public class HaAdapter extends ExpandBaseAdapter<HaAdapter.ViewHolder> {
    public HaAdapter(Context mContext, List<ExpandBean> mData) {
        super(mContext, mData);
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int level, View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position, ExpandBean expandBean) {
        String expandText = "未展开";
        if (expandBean.isExpand()) {
            expandText = "展开";
        }
        if (mShowData.get(position).getShowPos() != position) {
            holder.getTextView().setTextColor(Color.RED);
        } else {
            holder.getTextView().setTextColor(Color.BLACK);
        }
        String head = "";
        for (int i = 0; i < expandBean.getLevel(); i++) {
            head += ">>";
        }
        holder.getTextView().setText(head + position + "--" + mShowData.get(position).getShowPos() + "--" + expandBean.getObject().toString() + "--" + expandText);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }

        public TextView getTextView() {
            return mTextView;
        }
    }
}
