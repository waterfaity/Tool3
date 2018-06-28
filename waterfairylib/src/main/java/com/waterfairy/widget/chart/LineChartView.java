package com.waterfairy.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.waterfairy.widget.baseView.BaseView;
import com.waterfairy.widget.baseView.Coordinate;
import com.waterfairy.widget.baseView.Entity;
import com.waterfairy.widget.utils.CanvasUtils;
import com.waterfairy.widget.utils.RectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/8 13:45
 * @info:
 */
public class LineChartView extends BaseView {
    private int[] colors = new int[]{
            Color.parseColor("#2e13c6"),
            Color.parseColor("#f3685d"),
            Color.parseColor("#04912a"),
            Color.parseColor("#c904b5"),
            Color.parseColor("#008380"),
            Color.parseColor("#ec4f01"),
            Color.parseColor("#afac00"),
            Color.parseColor("#c40d00"),
            Color.parseColor("#ac7300"),
            Color.parseColor("#52af0b"),
            Color.parseColor("#04a2f7"),
            Color.parseColor("#ffb254"),
            Color.parseColor("#74c9f7"),
            Color.parseColor("#a260ff"),
            Color.parseColor("#ff62f5"),
            Color.parseColor("#fa629a"),
            Color.parseColor("#e1855d"),
            Color.parseColor("#5e59f9")
    };

    private static final String TAG = "lineChartView";
    //传入数据
    private List<List<Entity>> mDataList;//输入数据
    private int maxValue;
    //data
    private List<List<Coordinate>> mCoordinateList;//数据坐标
    private List<Coordinate> mDataInfoCoordinateList;//数据信息展示的坐标
    private List<Entity> mYLineList;//Y轴线坐标
    private int mWidthEntity;//间隔
    private int mTextSize;
    private boolean showInfo = true;//展示坐标的具体内容
    private boolean isShowingInfo;//展示坐标的具体内容的状态
    //坐标
    private int scrollX; //滚动距离
    private int maxScrollX;
    private int bottomLine;//底部y坐标
    private int leftLine, rightLine;//左侧x坐标
    private int topLine;
    private int yNum = 4;//y轴个数
    private int xMaxNum = 10;//x轴最大个数
    private int xNum;//x轴显示个数
    private int startPos = 0;//x轴开始绘制位置
    private int perWidth;//x间隔
    private int dNumDrawXText = 1;//每n个画x轴坐标

    private int textWidth, textHeight;
    private int chinaTextWidth;
    private float perValueHeight;
    private int radius;
    //画笔
    private Paint mPaintLineY;
    private Paint mPaintText;
    private Paint mPaintLine;
    //rect
    private RectF mInfoRect;
    //data
    private boolean parentDisTouch = false;
    private ArrayList<String> xTitles;
    private int mSelectPos;
    //颜色
    private int mColorInfoBg;
    private int mColorNormalText;
    //titles
    private List<String> typeTitles;
    private ArrayList<String> titlesWithScore;
    //多类型
    private boolean isMulti;
    public static final int TYPE_DIR_NONE = 0;
    public static final int TYPE_DIR_VER = 1;
    public static final int TYPE_DIR_HOR = 2;
    private int typeTextDir;//类型文字方向
    private float mRightTypeWidth;
    private float mBottomTypeWidth;
    private RectF typeTextListRectF;//类型文本边框


    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getDensity();
        initText();
        initColor();
        initPaint();
    }

    private void initColor() {
        mColorInfoBg = Color.parseColor("#CCFFFFFF");
        mColorNormalText = Color.parseColor("#454545");
    }

    /**
     * 计算文本相关
     */
    private void initText() {
        mTextSize = (int) (getDensity() * 12);
        Rect textRect = getTextRect("0", mTextSize);
        textHeight = Math.abs(textRect.bottom + textRect.top);
        textWidth = textRect.right + textRect.left;
        chinaTextWidth = getTextRect("正", mTextSize).width();
    }

    private void initPaint() {
        mPaintLineY = new Paint();
        mPaintLineY.setColor(Color.parseColor("#999999"));
        mPaintLineY.setStrokeWidth(density);

        mPaintText = new Paint();
        mPaintText.setColor(mColorNormalText);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setAntiAlias(true);

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#289456"));
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(density * 1);


    }

    public void iniLineColor(int color) {
        mPaintLine.setColor(color);
    }

    public List<List<Entity>> getDataList() {
        return mDataList;
    }

    public void addData(List<Entity> dataList, int maxValue) {
        if (mDataList == null) mDataList = new ArrayList<>();
        mDataList.add(dataList);

        setData(mDataList, maxValue);
    }

    public void setSingleData(List<Entity> dataList, int maxValue) {
        mDataList = new ArrayList<>();
        mDataList.add(dataList);
        setData(mDataList, maxValue);
    }

    public void setData(List<List<Entity>> mDataList, int maxValue) {
        this.maxValue = maxValue;
        this.mDataList = mDataList;
        isMulti = mDataList.size() > 1;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
        if (width != 0 && height != 0) {
            calcData();
        }
    }

    /**
     * 计算数据坐标
     */
    private void calcData() {
        //计算 typeText
        calcTypeText();
        //一般计算
        calcNormal();
        //计算y轴
        calcY();
        //计算x轴
        calcX();
        reCalcTypeText();
    }

    private void reCalcTypeText() {
        if (typeTextListRectF != null) {
            typeTextListRectF.left += rightLine;
            typeTextListRectF.right += rightLine;
            int dx = bottomLine - topLine;
            if (typeTextListRectF.height() < (dx)) {
                float dx2 = (dx - typeTextListRectF.height()) / 2;
                typeTextListRectF.top += dx2;
                typeTextListRectF.bottom += dx2;
            }
        }
    }

    private void calcTypeText() {
        if (typeTitles != null && typeTitles.size() > 0 && typeTextDir != TYPE_DIR_NONE) {
            typeTextListRectF = RectUtils.getTextListRectF(typeTitles, mTextSize, 0.5F, mTextSize);
            mRightTypeWidth = typeTextListRectF.width();
        }
    }


    private void calcNormal() {
        if (yNum < 1) yNum = 1;
        if (maxValue <= 0) maxValue = 100;
        rightLine = (int) (width - textWidth * 2 - getPaddingRight() - mRightTypeWidth);

        leftLine = (("" + maxValue).length() + 2) * textWidth + getPaddingLeft();
        radius = (int) (density * 2.5);
    }

    private void calcY() {
        //计算y轴坐标 上留 一个字符高度  下留2个字符高度
        for (int i = 0; i < yNum; i++) {
            if ((maxValue + i) % yNum == 0) {
                int tempMaxValue = maxValue + i;
                //设置底部线
                bottomLine = height - 3 * textHeight - getPaddingBottom();
                topLine = textHeight * 2 + getPaddingTop();
                int perHeight = (bottomLine - topLine) / yNum;
                perValueHeight = (bottomLine - topLine) / (float) tempMaxValue;
                mYLineList = new ArrayList<>();
                for (int j = 0; j < yNum + 1; j++) {
                    Entity entity = new Entity();
                    //y坐标
                    entity.value = bottomLine - j * perHeight;
                    //y显示文字
                    entity.name = tempMaxValue / yNum * j + "";
                    mYLineList.add(entity);
                }
            }
        }
    }

    //
//    private void calcX() {
//        if (mDataList != null && mDataList.size() > 0 && mDataList.get(0).size() > 0) {
//            //计算坐标
//            mCoordinateList = new ArrayList<>();
//            int tempLineWidth = rightLine - leftLine;
//            xNum = mDataList.get(0).size();
//            if (xNum > xMaxNum) xNum = xMaxNum;
//            if (xNum == 1) {
//                //只有一个
//                addCoordinateData(1);
//            } else {
//                perWidth = tempLineWidth / (xNum - 1);
//                addCoordinateData(mDataList.get(0).size());
//            }
//        }
//    }
    private void calcX() {
        if (mDataList != null && mDataList.size() > 0) {
            List<Entity> entities = mDataList.get(0);
            if (entities != null && entities.size() > 0) {

            }
            //计算坐标
            mCoordinateList = new ArrayList<>();
            int tempLineWidth = rightLine - leftLine;
            xNum = entities.size();
            if (xNum > xMaxNum) xNum = xMaxNum;
            if (xNum == 1) {
                //只有一个
                for (int i = 0; i < mDataList.size(); i++) {
                    List<Entity> entitiesTemp = mDataList.get(i);
                    Entity entity = entitiesTemp.get(0);
                    List<Coordinate> list = new ArrayList<>();
                    list.add(new Coordinate(leftLine + (tempLineWidth / 2), (int) (bottomLine - entity.getValue() * perValueHeight), entity.getValue(), entity.getName()));
                    mCoordinateList.add(list);
                }

            } else {
                perWidth = tempLineWidth / (xNum - 1);
                for (int j = 0; j < mDataList.size(); j++) {
                    List<Entity> entityList = mDataList.get(j);
                    List<Coordinate> list = new ArrayList<>();
                    for (int i = 0; i < entityList.size(); i++) {
                        Entity entity = entityList.get(i);
                        list.add(new Coordinate(leftLine + i * perWidth, (int) (bottomLine - entity.getValue() * perValueHeight), entity.getValue(), entity.getName()));
                    }
                    mCoordinateList.add(list);
                }

                maxScrollX = perWidth * (mCoordinateList.get(0).size() - xNum);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintText.setColor(mColorNormalText);
        //绘制y轴
        drawY(canvas);
        //绘制typeText
        drawTypeText(canvas);
        //绘制x轴
        drawX(canvas);
        //绘制提示信息
        drawInfo(canvas);
    }

    private void drawTypeText(Canvas canvas) {
        CanvasUtils.drawTextList(canvas, typeTextListRectF, typeTitles, colors, mColorNormalText, mTextSize, mPaintText, mTextSize, true);
    }

    private void drawInfo(Canvas canvas) {
        if (showInfo && isShowingInfo && typeTitles != null) {

//            mPaintLine.setColor(mColorNormalText);
//            mPaintLine.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(mInfoRect, mPaintLine);
//
//            mPaintLine.setStyle(Paint.Style.FILL);
//            mPaintLine.setColor(mColorInfoBg);
//            canvas.drawRect(mInfoRect, mPaintLine);

            CanvasUtils.drawCorner(canvas, mInfoRect, 10, 2, mColorNormalText, mColorInfoBg, mPaintLine);

            CanvasUtils.drawTextList(canvas, mInfoRect, titlesWithScore, colors, mTextSize, mPaintText, 10);
//
//            for (int i = 0; i < mDataList.size(); i++) {
//                List<Entity> entities = mDataList.get(i);
//                String title = "";
//                if (typeTitles.size() > i) title = typeTitles.get(i);
//                if (entities != null && entities.size() > mSelectPos) {
//                    Entity entity = entities.get(mSelectPos);
//                    String text = title + ":" + entity.getValue();
//                    mPaintText.setColor(colors[i % colors.length]);
//                    canvas.drawText(text, mInfoRect.left + chinaTextWidth / 2, mInfoRect.top + chinaTextWidth * 1.5F + i * (chinaTextWidth * 1.1F), mPaintText);
//                }
//            }
        }
    }

    private void drawX(Canvas canvas) {
        if (mCoordinateList == null || mCoordinateList.size() == 0) return;
        List<Coordinate> coordinates = mCoordinateList.get(0);
        if (coordinates == null || coordinates.size() == 0) return;
        int maxPos = startPos + xNum;
        if (dNumDrawXText < 0) dNumDrawXText = 1;
        for (int j = 0; j < mCoordinateList.size(); j++) {
            mPaintLine.setColor(colors[j % colors.length]);
            List<Coordinate> coordinateList = mCoordinateList.get(j);
            for (int i = startPos; i < maxPos + 1 && i < coordinateList.size(); i++) {
                Coordinate coordinate = coordinateList.get(i);
                int x = coordinate.x + scrollX;
                boolean overSideLeft = false;
                boolean overSideRight = false;
                if (x < leftLine) overSideLeft = true;
                if (x > rightLine) overSideRight = true;
                if (!overSideLeft) {
                    //没有超出左边界
                    // 绘制x坐标刻度
                    if (!overSideRight) {
                        canvas.drawLine(x, bottomLine, x, bottomLine + textHeight / 2, mPaintLineY);
                    }
                    //绘制圆点
                    canvas.drawCircle(x, coordinate.y, radius, mPaintLine);
                    //绘制数据值
                    if (!isMulti) {
                        Rect textRect = getTextRect(coordinate.value + "", mTextSize);
                        int widthHalf = textRect.width() / 2;
                        canvas.drawText(coordinate.value + "", x - widthHalf, coordinate.y - textHeight, mPaintText);
                    }
                    if (i != maxPos && (i + 1 < coordinateList.size())) {
                        //画折线
                        Coordinate coordinate2 = coordinateList.get(i + 1);
                        canvas.drawLine(x, coordinate.y, coordinate2.x + scrollX, coordinate2.y, mPaintLine);
                    }
                }

                if (i % dNumDrawXText == 0 && !TextUtils.isEmpty(coordinate.text) && j == 0) {
                    Rect textRect = getTextRect(coordinate.text, mTextSize);
                    int width = textRect.right + Math.abs(textRect.left);
                    int height = Math.abs(textRect.bottom - textRect.top);
                    canvas.drawText(coordinate.text, x - width / 2, bottomLine + height * 2F, mPaintText);
                }
            }
        }


    }

    private void drawY(Canvas canvas) {
        if (mYLineList == null) return;
        for (int i = 0; i < mYLineList.size(); i++) {
            Entity entity = mYLineList.get(i);
            canvas.drawLine(leftLine, entity.getValue(), rightLine, entity.getValue(), mPaintLineY);
            canvas.drawText(entity.getName(), textWidth + getPaddingLeft(), entity.getValue() + textHeight / 2, mPaintText);
        }
    }


    public void setNumDrawXText(int dNumDrawXText) {
        this.dNumDrawXText = dNumDrawXText;
    }

    float lastMoveX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastMoveX = event.getX();
            getParent().requestDisallowInterceptTouchEvent(parentDisTouch);
        } else if (action == MotionEvent.ACTION_MOVE) {
            handleMove(event);
        } else if (action == MotionEvent.ACTION_UP) {
            float touchX = event.getX() + Math.abs(scrollX);
            if (mCoordinateList != null && mCoordinateList.size() > 0) {
                List<Coordinate> list = mCoordinateList.get(0);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Coordinate coordinate = list.get(i);
                        if (touchX > coordinate.x - perWidth / 2 && touchX < coordinate.x + perWidth / 2) {
                            mSelectPos = i;
                            if (onPosClickListener != null)
                                onPosClickListener.onLineClick(i, coordinate.x + scrollX, event.getY());
                            if (showInfo && typeTitles != null) {
                                onTouchPos(i, coordinate.x + scrollX, event.getY());
                                invalidate();
                            }

                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }

    //    private void onTouchPos(int pos, int x) {
//        if (isShowingInfo) {
//            isShowingInfo = false;
//        } else {
//            isShowingInfo = true;
//            //计算展示的位置
//            if (mDataList != null && mDataList.size() > 0) {
//
//
//                int maxWidth = 0, maxHeight = 0;
//                int lines = mDataList.size();
//                for (int i = 0; i < mDataList.size(); i++) {
//                    List<Entity> entities = mDataList.get(i);
//                    String title = "";
//                    if (typeTitles.size() > i) title = typeTitles.get(i);
//                    if (entities != null && entities.size() > pos) {
//                        Entity entity = entities.get(pos);
//                        String text = title + ":" + entity.getValue();
//                        maxWidth = Math.max(getTextRect(text, mTextSize).width(), maxWidth);
//                    }
//                }
//
//                maxWidth = maxWidth + getTextRect("正", mTextSize).width();//多一个字符作为边界
//                maxHeight = (int) (lines * (chinaTextWidth * 1.1F) + chinaTextWidth);
//                //
//                int halfWidth = maxWidth / 2;
//                //x到paddingleft 的距离
//                int dx1 = x - getPaddingLeft();
//                //x到paddingRight的距离
//                int dx2 = getWidth() - getPaddingRight() - x;
//
//                int infoStartX = 0, infoEndX = 0;
//                int infoTopY = 0, infoBottomY = 0;
//
//                if (dx1 < halfWidth) {
//                    infoStartX = getPaddingLeft();
//                    infoEndX = infoStartX + maxWidth;
//                } else if (dx2 < halfWidth) {
//                    infoEndX = getWidth() - getPaddingRight();
//                    infoStartX = infoEndX - maxWidth;
//                } else {
//                    infoStartX = x - halfWidth;
//                    infoEndX = x + halfWidth;
//                }
//                infoBottomY = centerY + maxHeight / 2;
//                infoTopY = centerY - maxHeight / 2;
//                mInfoRect = new RectF(infoStartX, infoTopY, infoEndX, infoBottomY);
//            }
//        }
//        invalidate();
//    }
    private void onTouchPos(int pos, int x, float y) {
        if (isShowingInfo) {
            isShowingInfo = false;
        } else {
            isShowingInfo = true;
            //计算展示的位置
            if (mDataList != null && mDataList.size() > 0) {


                titlesWithScore = new ArrayList<>();
                for (int i = 0; i < mDataList.size(); i++) {
                    List<Entity> entities = mDataList.get(i);
                    String title = "";
                    if (typeTitles.size() > i) title = typeTitles.get(i);
                    if (entities != null && entities.size() > pos) {
                        Entity entity = entities.get(pos);
                        String text = title + ":" + entity.getValue();
                        titlesWithScore.add(text);
                    }
                }
                mInfoRect = RectUtils.getTextListRectF(RectUtils.getTextListRectF(titlesWithScore, mTextSize, 0.2F, 10), x, y, leftLine, topLine, rightLine, bottomLine);
            }
        }
        invalidate();
    }

    private void handleMove(MotionEvent event) {
        float dx = event.getX() - lastMoveX;
        if (dx == 0 || xNum <= 1) return;
        scrollX += (event.getX() - lastMoveX);
        if (scrollX > 0) scrollX = 0;
        else if (scrollX < -maxScrollX) scrollX = -maxScrollX;
        startPos = -scrollX / perWidth;
        if (startPos < 0) startPos = 0;
        lastMoveX = event.getX();
        invalidate();
    }

    public void setParentDisTouch(boolean parentDisTouch) {
        this.parentDisTouch = parentDisTouch;
    }

    public void setXTitles(ArrayList<String> titles) {
        this.xTitles = titles;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    private OnPosClickListener onPosClickListener;

    public OnPosClickListener getOnPosClickListener() {
        return onPosClickListener;
    }

    public void setTypeTitles(List<String> typeTitles) {
        this.typeTitles = typeTitles;
    }

    public interface OnPosClickListener {
        void onLineClick(int pos, float x, float y);
    }

    public void setTypeTextDir(int typeTextDir) {
        this.typeTextDir = typeTextDir;
    }
}
