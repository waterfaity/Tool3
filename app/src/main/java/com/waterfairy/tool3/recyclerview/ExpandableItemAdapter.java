package com.waterfairy.tool3.recyclerview;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * Created by water_fairy on 2017/9/7.
 * 995637517@qq.com
 */

public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    public ExpandableItemAdapter(List<MultiItemEntity> data, int resLevel0, int resLevel1) {
        super(data);
        addItemType(TYPE_LEVEL_0, resLevel0);
        addItemType(TYPE_LEVEL_1, resLevel1);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        final int position = helper.getAdapterPosition();
        if (helper.getItemViewType() == TYPE_LEVEL_0) {
            final ExpandableLevel0Entity expandableLevel0Entity = (ExpandableLevel0Entity) item;
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandableLevel0Entity.isExpanded()) {
                        collapse(position);
                    } else {
                        expand(position);
                    }
                }
            });
        }
    }
}
