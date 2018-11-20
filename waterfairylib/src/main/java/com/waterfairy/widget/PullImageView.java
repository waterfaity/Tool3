package com.waterfairy.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waterfairy.bean.OptionBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/28 17:28
 * @info:
 */
public class PullImageView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {
    private int pullImg;
    private OptionBean[] optionBeans;
    private boolean hasInit;
    private int imgWidth;
    private int pullWidth;
    private int pullHeight;
    private int imgHeight;
    private int diverWidth;//图片间隔宽度

    private List<CoordinateBean> beanList;

    private HashMap<Integer, Rect> pullTargetHashMap;//拖拉的图片
    private final int PULL_TARGET_SRC = 0;
    private Rect mPullSrcRect;
    private Rect mMoveRect;
    private Bitmap pullBitmap;

    private int viewTop;
    private int rightNum;//正确得答案个数
    private int bottomHeight;

    public PullImageView(Context context) {
        this(context, null);
    }

    public PullImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    /**
     * 设置图片
     *
     * @param optionBeans
     * @param pullImg
     */
    public void setOptionsAndPullImg(OptionBean[] optionBeans, int pullImg) {
        this.optionBeans = optionBeans;
        this.pullImg = pullImg;
    }

    /**
     * 设置大小
     *
     * @param imgWidth
     * @param imgHeight
     * @param pullWidth
     * @param pullHeight
     */
    public void setSize(int imgWidth, int imgHeight, int diverWidth, int pullWidth, int pullHeight) {
        this.imgWidth = imgWidth;
        this.diverWidth = diverWidth;
        this.imgHeight = imgHeight;
        this.pullWidth = pullWidth;
        this.pullHeight = pullHeight;
    }

    public void setBottomHeight(int bottomHeight) {
        this.bottomHeight = bottomHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        if (width != 0 && height != 0) {
            if (!hasInit) {
                hasInit = true;
                calc();
            }
        }
    }

    private void calc() {
        viewTop = getHeight() - (imgHeight + bottomHeight);
        int width = getWidth();
        int num = optionBeans.length;
        int totalWidth = num * imgWidth + (num - 1) * diverWidth;
        int leftX = (width - totalWidth) / 2;
        beanList = new ArrayList<>();
        rightNum = 0;
        for (int i = 0; i < num; i++) {
            Rect rect = new Rect(leftX + (imgWidth + diverWidth) * i, viewTop, leftX + (imgWidth + diverWidth) * i + imgWidth, imgHeight + viewTop);
            if (optionBeans[i].isRight) {
                rightNum++;
            }
            beanList.add(new CoordinateBean(rect, optionBeans[i]));
        }

        pullTargetHashMap = new HashMap<>();
        pullBitmap = BitmapFactory.decodeResource(getResources(), pullImg);
        mPullSrcRect = new Rect(0, 0, pullBitmap.getWidth(), pullBitmap.getHeight());

        //最下面
//        int pullSrcLeftX = width / 2 - (pullWidth / 2);
//        pullTargetHashMap.put(PULL_TARGET_SRC, new Rect(pullSrcLeftX, viewTop + imgHeight + diverWidth * 2, pullSrcLeftX + pullWidth, imgHeight + diverWidth * 2 + pullHeight + viewTop));
        //右上角
        int pullSrcLeftX = width - pullWidth / 2 - pullWidth;
        int pullSrcTop = viewTop - pullWidth / 3 - pullWidth;
        pullTargetHashMap.put(PULL_TARGET_SRC, new Rect(pullSrcLeftX, pullSrcTop, pullSrcLeftX + pullWidth, pullHeight + pullSrcTop));

        if (onViewCreateListener != null) {
            CoordinateBean coordinateBean = beanList.get(0);
            onViewCreateListener.onViewCreate(coordinateBean.targetRect.left, coordinateBean.targetRect.bottom);
        }
    }

    private boolean canMove;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect rect = pullTargetHashMap.get(PULL_TARGET_SRC);
            canMove = event.getX() > rect.left && event.getX() < rect.right && event.getY() > rect.top && event.getY() < rect.bottom;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            move((int) event.getX(), (int) event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            up();
        }

        return true;
    }

    private void up() {
        if (canMove && mMoveRect != null && beanList != null) {
            canMove = false;
            for (int i = 0; i < beanList.size(); i++) {
                CoordinateBean coordinateBean = beanList.get(i);
                if (coordinateBean != null && coordinateBean.targetRect != null) {
                    if (mMoveRect.left > coordinateBean.targetRect.left &&
                            mMoveRect.right < coordinateBean.targetRect.right &&
                            mMoveRect.top > coordinateBean.targetRect.top &&
                            mMoveRect.bottom < coordinateBean.targetRect.bottom) {
                        //拖到指定图片区域
                        int halfWidth = pullWidth / 2;
                        int halfHeight = pullHeight / 2;
                        //正确添加
                        if (coordinateBean.optionBean.isRight)
                            pullTargetHashMap.put(i + 1, new Rect(coordinateBean.targetRect.centerX() - halfWidth, coordinateBean.targetRect.centerY() - halfHeight, coordinateBean.targetRect.centerX() + halfWidth, coordinateBean.targetRect.centerY() + halfHeight));
                        //回调
                        if (onItemSelectListener != null) {
                            onItemSelectListener.onItemSelect(i, coordinateBean.optionBean.isRight, pullTargetHashMap.size() == rightNum + 1);
                        }
                        break;
                    }
                }

            }
        }
        mMoveRect = null;
        invalidate();
    }

    private void move(int x, int y) {
        if (canMove) {
            int halfWidth = pullWidth / 2;
            int halfHeight = pullHeight / 2;
            if (mMoveRect == null) {
                mMoveRect = new Rect();
            }
            mMoveRect.set(x - halfWidth, y - halfHeight, x + halfWidth, y + halfHeight);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (beanList != null) {
            for (int i = 0; i < beanList.size(); i++) {
                CoordinateBean coordinateBean = beanList.get(i);
                canvas.drawBitmap(coordinateBean.bitmap, coordinateBean.srcRect, coordinateBean.targetRect, null);
            }

            if (pullTargetHashMap != null) {
                for (Integer integer : pullTargetHashMap.keySet()) {
                    Rect rect = pullTargetHashMap.get(integer);
                    canvas.drawBitmap(pullBitmap, mPullSrcRect, rect, null);
                }
            }
            if (mMoveRect != null) {
                canvas.drawBitmap(pullBitmap, mPullSrcRect, mMoveRect, null);
            }
        }
    }

    public class CoordinateBean {
        public Rect targetRect;
        public Rect srcRect;
        public Bitmap bitmap;
        public OptionBean optionBean;

        public CoordinateBean(Rect targetRect, OptionBean optionBean) {
            this.targetRect = targetRect;
            this.optionBean = optionBean;
            bitmap = BitmapFactory.decodeResource(getResources(), optionBean.imgRes);
            srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    private OnItemSelectListener onItemSelectListener;

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {
        void onItemSelect(int pos, boolean right, boolean allRight);
    }

    private OnViewCreateListener onViewCreateListener;

    public void setOnViewCreateListener(OnViewCreateListener onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
    }

    public interface OnViewCreateListener {
        void onViewCreate(int bottomX, int bottomY);
    }
}
