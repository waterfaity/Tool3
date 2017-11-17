package com.waterfairy.widget.recyclerview.expand;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/16
 * @Description:
 */

public abstract class ExpandBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private boolean canClick = true;
    private static final String TAG = "ExpandBaseAdapter";
    protected List<ExpandBean> mData;//全部数据
    protected List<ExpandBean> mShowData;//显示的数据
    protected Context mContext;
    protected int[] mResIds;//资源ids
    protected int mLastLevel;//最后一级
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onLongClickListener;


    public ExpandBaseAdapter(Context mContext, List<ExpandBean> mData) {
        this.mData = mData;
        this.mContext = mContext;
        mShowData = new ArrayList<>();
        addTempPos(mData);
    }

    private void addTempPos(List<ExpandBean> mData) {
        if (mData != null)
            for (int i = 0; i < mData.size(); i++) {
                ExpandBean expandBean = mData.get(i).setShowPos(mShowData.size());
                mShowData.add(expandBean);
                if (expandBean.isExpand()) {
                    addTempPos(expandBean.getChildExpandBean());
                }
            }
    }

    @Override
    public int getItemCount() {
        return mShowData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mShowData.get(position).getLevel();
    }

    public void setResIds(int[] resIds) {
        mResIds = resIds;
        mLastLevel = resIds.length - 1;
    }

    public void notifyItemDelete(int pos) {

    }

    public void notifyItemDismiss() {


    }

    public void notifyItemShow() {

    }

    public Object getObject(int position) {
        return mShowData.get(position).getObject();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(mResIds[viewType], parent, false);
        return onCreateViewHolder2(parent, viewType, inflate);
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        ExpandBean expandBean = mShowData.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    canClick = false;
                    clickDelay.removeMessages(0);
                    clickDelay.sendEmptyMessageDelayed(0, 600);
                    int position = (Integer) v.getTag();
                    showData(position);
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(mShowData.get(position), position);
                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = (Integer) v.getTag();
                return onLongClickListener != null && onLongClickListener.onItemClick(mShowData.get(position), position);
            }
        });
        onBindViewHolder2(holder, position, expandBean);
    }

    Handler clickDelay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            canClick = true;
        }
    };

    protected abstract VH onCreateViewHolder2(ViewGroup parent, int level, View itemView);

    protected abstract void onBindViewHolder2(VH holder, int position, ExpandBean dataBean);

    private void showData(int position) {
        ExpandBean expandBean = mShowData.get(position);
        if (expandBean.getLevel() == mLastLevel) return;
        boolean isExpand = expandBean.isExpand();
        if (isExpand) {
            //去合并
            unExpand(expandBean, position);
        } else {
            //去展开
            expand(expandBean, position);
        }
    }

    private void unExpand(ExpandBean expandBean, int position) {
        List<ExpandBean> expandBeans = expandBean.unExpand();
        notifyItemChanged(position);
        if (expandBeans.size() == 0) return;//没有展开的子类
        mShowData.removeAll(expandBeans);
        final int from = position + 1;
        notifyItemRangeRemoved(from, expandBeans.size());
        for (int j = from; j < mShowData.size(); j++) {
            mShowData.get(j).setShowPos(j);
        }
        notifyItemRangeChanged(from, mShowData.size() - from);
    }

    private void expand(ExpandBean expandBean, final int position) {
        //去显示\
        List<ExpandBean> expandBeans = expandBean.expand();
        notifyItemChanged(position);
        if (expandBeans.size() == 0) return;//没有展开的子类
        //添加最新
        for (int i = 0; i < expandBeans.size(); i++) {
            ExpandBean expandBeanTemp = expandBeans.get(i);
            int currentPos = position + (i + 1);
            expandBeanTemp.setShowPos(currentPos);
            mShowData.add(currentPos, expandBeanTemp);
        }
        notifyItemRangeInserted(position + 1, expandBeans.size());
        //更新添加之后的位置
        final int from = position + expandBeans.size() + 1;
        for (int j = from; j < mShowData.size(); j++) {
            mShowData.get(j).setShowPos(j);
        }
        notifyItemRangeChanged(from, mShowData.size() - from);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnLongClickListener(OnItemLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ExpandBean expandBean, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(ExpandBean expandBean, int position);
    }
}
