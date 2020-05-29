package com.waterfairy.widget.recyclerview;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by water_fairy on 2017/9/7.
 * 995637517@qq.com
 */

public class ExpandableRecyclerView extends SwipeRefreshLayout implements OnScrollListener {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private View mTopView, mFootView;
    private int mTopRes, mFootRes;


    public ExpandableRecyclerView(Context context) {
        this(context, null);
    }

    public ExpandableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setProgressBackgroundColorSchemeResource(android.R.color.white);
        setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        initView(null);
    }

    public void initView(ScrollView scrollView) {

        removeAllViews();
        RelativeLayout swipeContent = new RelativeLayout(mContext);
        mTopView = createTopOrFootView(swipeContent, mTopRes, RelativeLayout.ALIGN_PARENT_TOP);
        if (scrollView != null) {
            swipeContent.addView(scrollView.getView());
            scrollView.setBottomListener(this);
        }
        mFootView = createTopOrFootView(swipeContent, mFootRes, RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(swipeContent);
    }

    private View createTopOrFootView(RelativeLayout swipeContent, int viewRes, int dir) {
        View content = null;
        if (viewRes == 0) {
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setText("加载中...");
            content = textView;
        } else {
            content = LayoutInflater.from(mContext).inflate(viewRes, null, false);
        }
        swipeContent.addView(content);
        int height = (int) (40 * getResources().getDisplayMetrics().density);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) content.getLayoutParams();
        layoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        layoutParams.addRule(dir, RelativeLayout.TRUE);
        content.setLayoutParams(layoutParams);
        return content;
    }


    public void setTopRes(int mTopRes) {
        this.mTopRes = mTopRes;
    }

    public void setFootRes(int mFootRes) {
        this.mFootRes = mFootRes;
    }

    public View getTopView() {
        return mTopView;
    }

    public View getFootView() {
        return mFootView;
    }

    @Override
    public void onScrollBottom() {
        setEnabled(true);
    }

    @Override
    public void onScrollTop() {
        setEnabled(true);
    }
}
