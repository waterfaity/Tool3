package com.waterfairy.widget.recyclerView.expand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2017/11/16
 * @Description:
 */

public class ExpandBean {
    private static final int ACTION_UN_EXPAND = 1;
    private static final int ACTION_EXPAND = 2;
    private static final int ACTION_DELETE = 3;
    private static final String TAG = "expandBean";
    //    private boolean visibility = true;//是否被隐藏
//    private boolean delete = false;//是否被删除
    private int pos;//位置各等级 pos 叠加
    private int showPos;//显示的位置
    private int level;//级别0,1,2...
    private Object object;//数据
    private List<ExpandBean> childExpandList;//包含的位置
    private boolean isExpand = false;//是否展开
    private static List<ExpandBean> expandBeans;//处理得出所有需要处理的数据

    public ExpandBean(int pos) {
        this.pos = pos;
    }

    public ExpandBean() {
    }

    public List<ExpandBean> unExpand() {
        setExpand(false);
        return handleData(ACTION_UN_EXPAND, false);
    }

    public List<ExpandBean> expand() {
        setExpand(true);
        return handleData(ACTION_EXPAND, false);
    }

    private List<ExpandBean> delete() {
        return handleData(ACTION_DELETE, true);
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    /**
     * @param type        处理指令
     * @param contentSelf 是否处理自己(expand/unExpand  不处理自己)
     * @return
     */
    private List<ExpandBean> handleData(int type, boolean contentSelf) {
        expandBeans = new ArrayList<>();
        if (contentSelf)
            handleOne(type, this);
        handle(type);
        return expandBeans;
    }

    /**
     * 处理单个
     *
     * @param type       指令
     * @param expandBean 处理的expandBean
     */
    private void handleOne(int type, ExpandBean expandBean) {
//        switch (type) {
//            case ACTION_UN_EXPAND:
//                if (!expandBean.isVisibility()) return;
//                expandBean.setVisibility(false);
//                break;
//            case ACTION_EXPAND:
//                if (expandBean.isVisibility()) return;
//                expandBean.setVisibility(true);
//                break;
//            case ACTION_DELETE:
//                if (expandBean.isDelete()) return;
//                expandBean.setDelete(true);
//                break;
//        }
        expandBeans.add(expandBean);
    }

    /**
     * 开始处理
     *
     * @param type
     * @return
     */
    private List<ExpandBean> handle(int type) {
        if (childExpandList != null) {
            for (ExpandBean aChildExpandBean : childExpandList) {
                //处理当前
                handleOne(type, aChildExpandBean);
                //处理子类
                if (type == ACTION_EXPAND) continue;//展开操作不针对子类的子类
                if (type == ACTION_UN_EXPAND) {
                    if (!aChildExpandBean.isExpand()) {
                        continue;//合并操作不针对 已经是合并的子类
                    } else {
                        aChildExpandBean.setExpand(false);
                    }
                }
                aChildExpandBean.handle(type);
            }
        }
        return expandBeans;
    }

    public List<ExpandBean> addChild(ExpandBean childExpandBean) {
        if (childExpandList == null) {
            childExpandList = new ArrayList<>();
        }
        childExpandList.add(childExpandBean);
        return childExpandList;
    }

//    public boolean isDelete() {
//        return delete;
//    }

//    public void setDelete(boolean delete) {
//        this.delete = delete;
//    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public List<ExpandBean> getChildExpandBean() {
        return childExpandList;
    }

    public void setChildExpandBean(List<ExpandBean> childExpandBean) {
        this.childExpandList = childExpandBean;
    }

//    public boolean isVisibility() {
//        return visibility;
//    }

//    public void setVisibility(boolean visibility) {
//        this.visibility = visibility;
//    }

    public int getShowPos() {
        return showPos;
    }

    public ExpandBean setShowPos(int showPos) {
        this.showPos = showPos;
        return this;
    }
}
