package com.waterfairy.adapter;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/28
 * @Description: 用于上拉加载的RecyclerView的适配器
 */

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class PullToLoadAdapter extends RecyclerView.Adapter {
    private final int TYPE_FOOTER = -1;
    protected Context mContext;
    private RecyclerStatus status;
    /**
     * 锁住当前状态(防止多次加载) * 在网络加载时locked = true * 需要在网络加载成功后置为false
     */
    private boolean locked = false;
    /**
     * 是否出现上拉加载的状态提示条
     */
    private boolean pullLoadEnable = false;
    private OnProgressListener onProgressListener;
    private OnCompleteListener onCompleteListener;

    public PullToLoadAdapter(Context mContext) {
        this.mContext = mContext;
        status = RecyclerStatus.NONE;
    }

    @Override
    public int getItemCount() {
        if (pullLoadEnable) {
            return getItemCountChild() + 1;
        } else {
            return getItemCountChild();
        }
    }

    protected abstract int getItemCountChild();

    @Override
    public int getItemViewType(int position) {
        if (pullLoadEnable && position == getItemCountChild()) {
            return TYPE_FOOTER;
        }
        if (getItemViewTypeChild(position) == TYPE_FOOTER) {
            throw new IllegalArgumentException("type类型不能与底部条一样,请修改继承类的getItemViewTypeChild方法返回值");
        }
        return getItemViewTypeChild(position);
    }

    protected abstract int getItemViewTypeChild(int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View contentView = new RelativeLayout(mContext);
            return new FooterViewHolder(contentView);
        } else {
            return onCreateViewHolderChild(parent, viewType);
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateViewHolderChild(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).textView.setText(status.getName());
            if (status == RecyclerStatus.READY) {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.GONE);
            } else if (status == RecyclerStatus.LOADING) {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            } else {
                ((FooterViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
            ((FooterViewHolder) holder).textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onProgressListener != null) {
                        if (status == RecyclerStatus.ERROR) {
                            onProgressListener.tryAgain();
                        }
                    }
                    if (onCompleteListener != null) {
                        if (status == RecyclerStatus.COMPLETE) {
                            onCompleteListener.onComplete();
                        }
                    }
                }
            });
        } else {
            onBindViewHolderChild(holder, position);
        }
    }

    protected abstract void onBindViewHolderChild(RecyclerView.ViewHolder holder, int position);

    public void setLocked(boolean locked) {
        if (this.locked != locked) {
            this.locked = locked;
        }
    }

    public void setStatus(RecyclerStatus status) {
        if (this.status != status) {
            this.status = status;
            if (this.status == RecyclerStatus.NONE) {
                pullLoadEnable = false;
            } else {
                pullLoadEnable = true;
            }
        }
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void addBindRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView)) {
                    if (status == RecyclerStatus.READY) {
                        if (!locked) {
                            status = RecyclerStatus.LOADING;
                            notifyItemChanged(getItemCount() - 1);
                            locked = true;
                            onProgressListener.nextPage();
                        }
                    }
                }
            }
        });
    }

    /**
     * 判断当前RecyclerView是否滑动到最后 * * @param recyclerView * @return
     */
    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
            return true;
        }
        return false;
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ProgressBar progressBar;

        public FooterViewHolder(View itemView) {
            super(itemView);
            RelativeLayout linearLayout = (RelativeLayout) itemView;
            ViewGroup.LayoutParams itemViewParams = linearLayout.getLayoutParams();
            linearLayout.setBackgroundColor(Color.parseColor("#f4f4f4"));
            if (itemViewParams != null) {
                itemViewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                linearLayout.setLayoutParams(itemViewParams);
            }
            float density = mContext.getResources().getDisplayMetrics().density;
            linearLayout.setPadding((int) (density * 16), (int) (10 * density), (int) (density * 16), (int) (10 * density));
            //textView
            textView = new TextView(mContext);
            linearLayout.addView(textView);
            textView.setTextColor(Color.parseColor("#787878"));
            textView.setTextSize(13);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            RelativeLayout.LayoutParams textViewLayoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            textViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            textViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            textViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            textView.setLayoutParams(textViewLayoutParams);
            //progressBar
            progressBar = new ProgressBar(mContext);
            linearLayout.addView(progressBar);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            layoutParams.height = (int) (30 * density);
            layoutParams.width = (int) (30 * density);
            layoutParams.rightMargin = (int) (10 * density);
            progressBar.setLayoutParams(layoutParams);
        }
    }

    public interface OnProgressListener {
        void nextPage();

        void tryAgain();
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public enum RecyclerStatus {
        NONE(""), READY("继续上拉加载"), LOADING("加载中"), ERROR("点击重试"), COMPLETE("没有更多了");
        String name;

        RecyclerStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}