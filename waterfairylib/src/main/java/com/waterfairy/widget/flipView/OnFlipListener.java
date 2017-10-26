package com.waterfairy.widget.flipView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/10/25
 * @Description:
 */

public interface OnFlipListener {
    //选中页
    void onFlipPageSelect(int pos);

    /**
     * 滑到左侧边界
     */
    void onFlipLeftEdge();

    /**
     * 滑到右侧边界
     */
    void onFlipRightEdge();

}
