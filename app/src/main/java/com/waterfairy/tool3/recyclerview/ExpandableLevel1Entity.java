package com.waterfairy.tool3.recyclerview;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by water_fairy on 2017/9/7.
 * 995637517@qq.com
 */

public class ExpandableLevel1Entity extends AbstractExpandableItem implements MultiItemEntity {

    @Override
    public int getItemType() {
        return ExpandableItemAdapter.TYPE_LEVEL_1;
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
