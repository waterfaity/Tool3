package com.waterfairy.widget.moveView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Set;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/9/3 10:38
 * @info: 拖动排序
 */
public class PullImageSortView extends BaseMoveSelfView {
    private BaseImageBean[] targetBean;
    private HashMap<Integer, Object> hashMap;
    private boolean showTargetRectLine;//显示目标区域
    private boolean stopForRight;//只有正确排序时 才停留到答案区域
    private int currentPos;
    private boolean isCurrentRight;


    public PullImageSortView(Context context) {
        super(context);
    }

    /**
     * 设置正确是 停留在正确区域
     *
     * @param stopForRight
     */
    public void setStopForRight(boolean stopForRight) {
        this.stopForRight = stopForRight;
    }

    public PullImageSortView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullImageSortView setTargetBean(BaseImageBean[] targetBean) {
        this.targetBean = targetBean;
        return this;
    }

    /**
     * 显示目标区域
     *
     * @param showTargetRectLine
     */
    public void setShowTargetRectLine(boolean showTargetRectLine) {
        this.showTargetRectLine = showTargetRectLine;
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
        if (targetBean != null && showTargetRectLine) {
            Paint paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            for (int i = 0; i < targetBean.length; i++) {
                canvas.drawRect(targetBean[i].rectFSrc, paint);
            }
        }

    }

    @Override
    protected void onCalc() {
        super.onCalc();
        //计算拖动图片
        if (targetBean != null && targetBean.length > 0) {
            for (int i = 0; i < targetBean.length; i++) {
                targetBean[i].calcRect(super.width, super.height);
                targetBean[i].setTag("option_" + i);
            }
        }
        if (srcData != null) {
            for (int i = 0; i < srcData.length; i++) {
                srcData[i].setTag("option_" + i);
            }
        }
    }

    @Override
    protected void onDownOption(BaseImageBean imageBean, int pos) {
        super.onDownOption(imageBean, pos);

    }

    @Override
    protected boolean handleUpAction(MotionEvent event) {
        isCurrentRight = false;
        currentPos = -1;
        //计算死否在指定的框内
        //是
        //  如果框中没有图片 添加到框中
        //  如果有  复位
        //否 复位
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        if (targetBean != null) {
            //匹配当前移动的图片
            //是否有移动的图片
            boolean hasMove = false;
            for (int i = 0; i < targetBean.length; i++) {
                BaseImageBean baseImageBean = targetBean[i];
                Rect rectFMove = currentImageBean.rectFMove;
                Rect rectFSrc = baseImageBean.rectFSrc;
                if (((rectFSrc.top > rectFMove.top && rectFSrc.top < rectFMove.bottom) ||
                        (rectFSrc.bottom > rectFMove.top && rectFSrc.bottom < rectFMove.bottom) ||
                        (rectFSrc.top < rectFMove.top && rectFSrc.bottom > rectFMove.bottom)) &&
                        (rectFSrc.left > rectFMove.left && rectFSrc.left < rectFMove.right ||
                                (rectFSrc.right > rectFMove.left && rectFSrc.right < rectFMove.right) ||
                                (rectFSrc.left < rectFMove.left && rectFSrc.right > rectFMove.right))) {
                    currentPos = i;
                    if (isCurrentRight = TextUtils.equals((String) currentImageBean.getTag(), (String) baseImageBean.getTag())) {
                        //正确
                    } else {
                        //错误
                        if (stopForRight) {
                            return false;
                        }
                    }

                    //移动到 A 的位置

                    //移出 A 位置之前的数据 ; 如果A的旧数据就是当前的移动的数据  则不用处理
                    Object tag = hashMap.get(i);
                    BaseImageBean aImageBean = null;
                    if (tag != null) {
                        if (!TextUtils.equals((String) tag, (String) currentImageBean.getTag())) {
                            //替换A位置
                            for (BaseImageBean aSrcData : srcData) {
                                if (TextUtils.equals((String) aSrcData.getTag(), (String) tag)) {
                                    //A的旧数据
                                    aImageBean = aSrcData;
                                    aSrcData.rectFMove = new Rect(aSrcData.rectFSrc);
                                    aSrcData.rectFMoveCopy = new Rect(aSrcData.rectFSrc);
                                    aSrcData.rectFSrc = new Rect(aSrcData.rectFSrcCopy);
                                    aSrcData.isMoving = true;
                                    hashMap.remove(i);
                                }
                            }
                        } else {
                            //回到A位置
                            hasMove = true;
                        }
                    }

                    if (hashMap.get(i) == null) {
                        //如果是从B位置移动到A位置   则移出在B的占位.
                        Set<Integer> integers = hashMap.keySet();
                        for (Integer integer : integers) {
                            if (TextUtils.equals((String) hashMap.get(integer), (String) currentImageBean.getTag())) {
                                //移出B的旧数据
                                hashMap.remove(integer);
                                //旧A的数据替换B的数据
                                if (aImageBean != null) {
                                    aImageBean.rectFSrc = new Rect(targetBean[integer].rectFSrc);
                                    aImageBean.isMoving = true;
                                    hashMap.put(integer, aImageBean.getTag());

                                }
                                break;
                            }
                        }
                        //添加
                        hashMap.put(i, currentImageBean.getTag());
                        currentImageBean.rectFSrc = new Rect(baseImageBean.rectFSrc);
                        hasMove = true;
                    }
                    break;
                }
            }
            //没有需要移动的 则复位
            if (!hasMove) {
                //判断是否有移动记录 有则移出
                Set<Integer> integers = hashMap.keySet();
                for (Integer integer : integers) {
                    if (TextUtils.equals((String) hashMap.get(integer), (String) currentImageBean.getTag())) {
                        hashMap.remove(integer);
                        //复位
                        currentImageBean.rectFSrc = new Rect(currentImageBean.rectFSrcCopy);
                        break;
                    }
                }
            }
        }
        return false;
    }

    private void handleSort() {
        if (onSortListener != null) {
            boolean complete = hashMap.size() == targetBean.length;
            boolean isRight = true;
            if (complete) {
                for (int i = 0; i < targetBean.length; i++) {
                    if (!TextUtils.equals((String) hashMap.get(i), (String) targetBean[i].getTag())) {
                        isRight = false;
                    }
                }
            } else {
                isRight = false;
            }
            onSortListener.onSort(this, currentPos, this.isCurrentRight, complete, isRight);
        }
    }

    @Override
    protected void onResetAnimEnd() {
        super.onResetAnimEnd();
        handleSort();
    }

    private OnSortListener onSortListener;

    public void setOnSortListener(OnSortListener onSortListener) {
        this.onSortListener = onSortListener;
    }

    public interface OnSortListener {
        /**
         * @param view
         * @param currentPos
         * @param isCurrentRight
         * @param isComplete     全部排序
         * @param isAllRight     排序是否正确
         */
        void onSort(PullImageSortView view, int currentPos, boolean isCurrentRight, boolean isComplete, boolean isAllRight);
    }


}