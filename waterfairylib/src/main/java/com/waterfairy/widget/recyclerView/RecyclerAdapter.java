package com.waterfairy.widget.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by water_fairy on 2017/9/7.
 * 995637517@qq.com
 * listView适用  有top/foot  view
 */

public abstract class RecyclerAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List dataList;
    protected Context mContext;
    private int mHeadViewRes, mFootViewRes;
    private View mFootView, mTopView;
    public static final int TYPE_HEAD_VIEW = 1;
    public static final int TYPE_FOOT_VIEW = 2;

    public RecyclerAdapter(Context context, List dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resId = 0;
        View parentView = null;
        View contentView = null;
        if (viewType == TYPE_HEAD_VIEW) {
            resId = mHeadViewRes;
            if (resId == 0) {
                TextView topView = new TextView(mContext);
                topView.setText("加载中...");
                contentView = topView;
            } else {
                mTopView = LayoutInflater.from(mContext).inflate(resId, null, false);
                contentView = mTopView;
            }
        } else if (viewType == TYPE_FOOT_VIEW) {
            resId = mFootViewRes;
            if (resId == 0) {
                TextView footView = new TextView(mContext);
                footView.setText("加载中...");
                contentView = footView;
            } else {
                mFootView = LayoutInflater.from(mContext).inflate(resId, null, false);
                contentView = mFootView;
            }
        } else {
            return onCreateViewHolder2(parent, viewType);
        }

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(contentView);
        parentView = linearLayout;
        parent.addView(parentView);
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//        if (holder != null && position != 0 && (dataList != null && dataList.size() > 0 && position != dataList.size() + 1)) {
        if (holder != null) {
            onBindView(holder, position - 1);
        }
    }

    @Override
    public int getItemCount() {
        //第一个topView
        //最后一个footView
        int count = 0;
        if (dataList != null)
            count = dataList.size();
        return count + 2;
    }


    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (position == 0) type = TYPE_HEAD_VIEW;
        if (dataList != null && dataList.size() > 0) {
            type = TYPE_FOOT_VIEW;
        }
        return type;
    }

    public void setHeadViewRes(int mHeadViewRes) {
        this.mHeadViewRes = mHeadViewRes;
    }

    public void setFootViewRes(int mFootViewRes) {
        this.mFootViewRes = mFootViewRes;
    }

    public View getFootView() {
        return mFootView;
    }

    public View getTopView() {
        return mTopView;
    }


    /**
     * 数据展示
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    /**
     * 获取viewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType);

}
