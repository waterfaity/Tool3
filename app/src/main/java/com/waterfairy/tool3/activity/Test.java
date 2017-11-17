package com.waterfairy.tool3.activity;

import com.waterfairy.widget.recyclerView.expand.ExpandBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/16
 * @Description:
 */

public class Test {
    private static final String TAG = "Test";

    public static List<ExpandBean> getData() {
        List<ExpandBean> mData = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            //1
            ExpandBean expandBean = new ExpandBean();
            expandBean.setLevel(0);
            expandBean.setObject("*" + i);
            for (int j = 0; j < 4; j++) {
                //2
                ExpandBean expandBean1 = new ExpandBean();
                expandBean1.setLevel(1);
                expandBean1.setObject("*" + i + "--" + j);
                for (int k = 0; k < 4; k++) {
                    //3
                    ExpandBean expandBean2 = new ExpandBean();
                    expandBean2.setLevel(2);
                    expandBean2.setObject("*" + i + "--" + j + "--" + k);
                    expandBean1.addChild(expandBean2);
                }
                expandBean.addChild(expandBean1);
            }
            mData.add(expandBean);
        }
        return mData;
    }
}
