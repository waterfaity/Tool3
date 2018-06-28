package com.waterfairy.widget.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.waterfairy.library.R;
import com.waterfairy.widget.baseView.BaseView;
import com.waterfairy.widget.baseView.Coordinate;
import com.waterfairy.widget.baseView.Entity;
import com.waterfairy.widget.utils.CanvasUtils;
import com.waterfairy.widget.utils.CoordinateUtils;
import com.waterfairy.widget.utils.RectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/15 09:24
 * @info:
 */
public class RadarChartView extends BaseView {

    private static final String TAG = "radarChartView";
    private List<ItemBean> itemBeanList;//展示数据 折线
    private List<ItemBean> lineList;//背景数据  背景网格
    private List<Coordinate> axisTitleList;//轴title数据
    private int layerNum = 4;// 层次
    private int axisNum = 5;//轴个数
    private float centerX, centerY; //中心x,y
    private float maxRadius;//线最长长度
    private float perLen;//每value 长度
    private int startX, startY;
    //起点旋转角度
    private float transAngle;
    //paint
    private Paint textPaint;
    private Paint linePaint;
    private Paint frontPaint;
    //color
    private int normalTextColor;//文字颜色
    private int lineColor;//线颜色
    private int bgLineColor;//背景线颜色
    private int frontBgColor;//详细数据背景
    //textSize
    private float textSize;
    private List<String> axisTitles;
    //onDraw 前初始化数据
    private boolean hasInitData;
    private boolean showPart;//展示某模块数据
    private int selectPos;
    private float touchX, touchY;


    public RadarChartView(Context context) {
        this(context, null);
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarChartView);
        int num = typedArray.getInt(R.styleable.RadarChartView_axisNum, 0);
        if (num > 0) axisNum = num;
        initColor();
        initPaint();
        initData();
        initTestData();
        typedArray.recycle();
    }

    private void initTestData() {
        ItemBean itemBean = new ItemBean();
        itemBean.setTitle("数据");
        itemBean.setColor(Color.BLUE);
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < axisNum; i++) {
            entities.add(new Entity(1, "1"));
        }
        itemBean.setDataList(entities);
        addData(itemBean);
    }

    private void initData() {
        textSize = getDensity() * 11;
    }

    private void initColor() {
        this.normalTextColor = Color.parseColor("#666666");
        this.bgLineColor = Color.parseColor("#666666");
        this.lineColor = Color.parseColor("#3cc676");
        this.frontBgColor = Color.parseColor("#bbFFFFFF");
    }

    private void initPaint() {
        textPaint = new Paint();
        textPaint.setColor(normalTextColor);
        textPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);

        frontPaint = new Paint();
        frontPaint.setColor(frontBgColor);
        frontPaint.setAntiAlias(true);
    }

    public void initTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void initLayer(int layer) {
        this.layerNum = layer;
    }

    public void initAxisTitle(List<String> titles) {
        this.axisTitles = titles;
    }


    public void fresh() {
        if (!showPart) {
            hasInitData = false;
            requestLayout();
        }
        invalidate();
    }

    public void addData(ItemBean itemBean) {
        if (this.itemBeanList == null) this.itemBeanList = new ArrayList<>();
        this.itemBeanList.add(itemBean);
    }


    public void addData(String title, int color, List<Entity> dataList) {
        if (this.itemBeanList == null) this.itemBeanList = new ArrayList<>();
        this.itemBeanList.add(new ItemBean(title, color, dataList));
    }

    public void removeData(String title) {
        if (!TextUtils.isEmpty(title) && this.itemBeanList != null) {
            boolean hasMove = false;
            for (int i = 0; i < this.itemBeanList.size(); i++) {
                ItemBean itemBean = this.itemBeanList.get(i);
                if (TextUtils.equals(itemBean.getTitle(), title)) {
                    itemBeanList.remove(itemBean);
                    hasMove = true;
                    i = i - 1;
                }
            }
            if (hasMove) {
                fresh();
            }
        }
    }

    @Override
    public void onViewMeasure(boolean changed, int width, int height) {
        if (hasInitData) return;
        //可用范围
        super.width = width - (getPaddingLeft() + getPaddingRight());
        super.height = height - (getPaddingTop() + getPaddingBottom());

        centerX = super.width / 2F + getPaddingLeft();
        centerY = super.height / 2F + getPaddingTop();
        maxRadius = Math.min(width, height) * 9 / 24F;

        startX = getPaddingLeft();
        startY = getPaddingTop();

        calcData();

    }

    private void calcData() {
        if (this.itemBeanList == null) return;
        //获取最大值
        int maxValue = 0;
        for (int i = 0; i < this.itemBeanList.size(); i++) {
            ItemBean itemBean = this.itemBeanList.get(i);
            if (itemBean != null) {
                List<Entity> dataList = itemBean.getDataList();
                if (dataList != null) {
                    axisNum = Math.max(axisNum, dataList.size());
                    for (int j = 0; j < dataList.size(); j++) {
                        Entity entity = dataList.get(j);
                        maxValue = Math.max(entity.getABSValue(), maxValue);
                    }
                }
            }
        }

        //轴数据为0
        if (axisNum == 0) return;

        //角度旋转
        transAngle = -90 - (180F / axisNum);

        //计算雷达图背景网格图
        //计算最大值
        float maxValueTemp = 0;
        for (int i = 0; i < layerNum; i++) {
            //计算layer 倍数的 高于maxValue 最小数值
            if ((maxValue + i) % layerNum == 0) {
                maxValueTemp = maxValue + i;
                //每份数据值 长度
                perLen = maxRadius / (maxValueTemp);
                lineList = new ArrayList<>();
                //层之间的  -数据- 差
                int perLayerValue = (maxValue + i) / layerNum;
                for (int j = 1; j < layerNum + 1; j++) {
                    //层
                    List<Coordinate> coordinates = new ArrayList<>();
                    ItemBean itemBean = new ItemBean();
                    for (int k = 0; k < axisNum; k++) {
                        //轴
                        int axisValue = j * perLayerValue;
                        Coordinate coordinate = getCoordinate(axisValue, k);
                        if (k == 0) {
                            // 第一个数据  用于绘制刻度
                            coordinate.value = axisValue;
                            coordinate.text = axisValue + "";
                        }
                        coordinates.add(coordinate);
                    }
                    itemBean.setColor(bgLineColor);
                    itemBean.setCoordinateList(coordinates);
                    lineList.add(itemBean);
                }
                break;
            }
        }

        Log.i(TAG, "calcData: " + (maxValueTemp));

        axisTitleList = new ArrayList<>();
        //轴title数据
        for (int i = 0; i < axisNum; i++) {
            //文本中心点
            Coordinate coordinate = getCoordinate(maxValueTemp * 57 / 48F, i);
            if (axisTitles != null && axisTitles.size() > i) {
                coordinate.text = axisTitles.get(i);
                //文本偏移计算
                Rect textRect = getTextRect(coordinate.text, (int) textSize);
                int textWidth = textRect.width();
                int textHeight = textRect.height();
                coordinate.x += (-textWidth / 2);
                coordinate.y += (textHeight / 2);
            }
            if (TextUtils.isEmpty(coordinate.text)) {
                coordinate.text = "";
            }
            axisTitleList.add(coordinate);
        }

        //展示数据 折线
        for (int i = 0; i < this.itemBeanList.size(); i++) {
            ItemBean itemBean = this.itemBeanList.get(i);
            if (itemBean != null) {
                List<Entity> dataList = itemBean.getDataList();
                if (dataList != null) {
                    List<Coordinate> coordinateList = new ArrayList<>();
                    for (int j = 0; j < dataList.size(); j++) {
                        int value = dataList.get(j).getValue();
                        coordinateList.add(getCoordinate(value<0?0:value, j));
                    }
                    itemBean.setCoordinateList(coordinateList);
                }
            }
        }
        //顶层数据

        //初始化完成
        hasInitData = true;
    }

    /**
     * @param value 数据
     * @param pos   位置
     * @return
     */
    private Coordinate getCoordinate(float value, int pos) {
        //半径长度
        float lineLen = value * perLen;
        return CoordinateUtils.calcWithRadius(-90 + 360F * (pos / (float) axisNum), lineLen, centerX, centerY);
    }

    /**
     * @param value 数据
     * @param pos   位置
     * @return
     */
    private Coordinate getCoordinateFromZero(int value, int pos) {
        //半径长度
        float lineLen = value * perLen;
        return CoordinateUtils.calcWithRadius(-90 + 360F * (pos / (float) axisNum), lineLen, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        linePaint.setStrokeWidth(1);
        //绘制背景网格
        drawBG(canvas, lineList, false);
        //绘制背景轴 和 轴title
        drawAxis(canvas);
        //绘制刻度值
        drawAxisText(canvas);
        //绘制分类
        drawType(canvas);

        linePaint.setStrokeWidth(2);
        //绘制数据图
        drawBG(canvas, itemBeanList, true);
        //顶层数据概况
//        drawDataDetail(canvas);
        drawDataDetail2(canvas);
    }

    private void drawDataDetail(Canvas canvas) {
        if (showPart && selectPos >= 0) {//展示详情 并且 有选中
            if (itemBeanList != null && itemBeanList.size() > 0) {//数据不为null  大小 大于0
                if (axisTitles != null && axisTitles.size() > selectPos) {//title 不为空 大小大于选择的pos

                    Rect textRect = getTextRect("正", (int) textSize);
                    int width = textRect.width();
                    //测试数据不纳入其中
                    int startPos = 0;
                    if (itemBeanList.size() > 1) startPos = 1;
                    if (startPos == 0) return;
                    //data
                    int dWidth = (int) (width * 3.5);
                    //画背景
                    frontPaint.setColor(frontBgColor);
                    frontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawRect((int) (centerX - dWidth), (int) (centerY - dWidth), (int) (centerX + dWidth), (int) (centerY + dWidth), frontPaint);
                    //背景线
                    frontPaint.setStyle(Paint.Style.STROKE);
                    frontPaint.setStrokeWidth(2);
                    frontPaint.setColor(bgLineColor);
                    canvas.drawRect((int) (centerX - dWidth), (int) (centerY - dWidth), (int) (centerX + dWidth), (int) (centerY + dWidth), frontPaint);
                    //title
                    canvas.drawText(axisTitles.get(selectPos), centerX - width * 3, (int) (centerY - width * 2), textPaint);
                    //content
                    for (int i = startPos; i < itemBeanList.size(); i++) {
                        int y = (int) (centerY - width * 2 + i * width * 7 / 5F);
                        ItemBean itemBean = itemBeanList.get(i);
                        String title = itemBean.getTitle();
                        List<Entity> dataList = itemBean.getDataList();
                        Entity entity = dataList.get(selectPos);
                        textPaint.setColor(itemBean.getColor());
                        canvas.drawText(title + ":" + entity.getValue(), centerX - width * 3, y + width / 4, textPaint);
                    }
                }

            }
        }
    }

    private void drawDataDetail2(Canvas canvas) {
        if (showPart && selectPos >= 0) {//展示详情 并且 有选中
            if (itemBeanList != null && itemBeanList.size() > 0) {//数据不为null  大小 大于0
                if (axisTitles != null && axisTitles.size() > selectPos) {//title 不为空 大小大于选择的pos

                    //测试数据不纳入其中
                    int startPos = 0;
                    if (itemBeanList.size() > 1) startPos = 1;
                    if (startPos == 0) return;

                    List<String> titles = new ArrayList<>();
                    int colors[] = new int[itemBeanList.size()];
                    colors[0] = normalTextColor;
                    titles.add(axisTitles.get(selectPos));

                    //content
                    for (int i = startPos; i < itemBeanList.size(); i++) {
                        ItemBean itemBean = itemBeanList.get(i);
                        String title = itemBean.getTitle();
                        List<Entity> dataList = itemBean.getDataList();
                        Entity entity = dataList.get(selectPos);
                        colors[i] = itemBean.getColor();
                        titles.add(title + ":" + entity.getValue());
                    }

                    RectF textListRectF = RectUtils.getTextListRectF(titles, textSize, 0.2F, 10);
                    RectF textListRectF1 = RectUtils.getTextListRectF(textListRectF, touchX, touchY);
                    CanvasUtils.drawCorner(canvas, textListRectF1, 10, 2, bgLineColor, frontBgColor, textPaint);
                    CanvasUtils.drawTextList(canvas, textListRectF1, titles, colors, textSize, textPaint, 10);
                }
            }
        }
    }

    //绘制分类 title
    private void drawType(Canvas canvas) {
        if (itemBeanList != null && itemBeanList.size() > 0) {
            Rect textRect = getTextRect("正", (int) textSize);
            int width = textRect.width();
            linePaint.setStrokeWidth(6);
            //测试数据不纳入其中
            int startPos = 0;
            if (itemBeanList.size() > 1) startPos = 1;
            for (int i = startPos; i < itemBeanList.size(); i++) {
                ItemBean itemBean = itemBeanList.get(i);
                linePaint.setColor(itemBean.getColor());
                int y = (int) (startY + width * 2 + i * width * 7 / 5F);
                //线
                canvas.drawLine(startX + width, y, startX + width * 2, y, linePaint);
                //文本
                canvas.drawText(itemBean.getTitle(), startX + width * 3, y + width / 4, textPaint);
            }
        }
    }

    //绘制刻度值
    private void drawAxisText(Canvas canvas) {
        if (lineList != null && lineList.size() > 0) {
            for (int i = 0; i < lineList.size(); i++) {
                ItemBean itemBean = lineList.get(i);
                List<Coordinate> coordinateList = itemBean.getCoordinateList();
                if (coordinateList != null) {
                    for (int j = 0; j < 1; j++) {
                        Coordinate coordinate = coordinateList.get(j);
                        //绘制刻度值
                        canvas.drawText(coordinate.text, coordinate.x + textSize / 2, coordinate.y + textSize / 2, textPaint);
                    }
                }
            }
        }
    }

    //绘制背景轴
    private void drawAxis(Canvas canvas) {
        if (lineList != null && lineList.size() > 0) {
            ItemBean itemBean = lineList.get(lineList.size() - 1);
            linePaint.setColor(itemBean.getColor());
            List<Coordinate> coordinateList = itemBean.getCoordinateList();
            if (coordinateList != null) {
                textPaint.setTextSize(textSize);
                textPaint.setColor(normalTextColor);
                for (int i = 0; i < coordinateList.size(); i++) {
                    Coordinate coordinate = coordinateList.get(i);
                    //圆心到外边的直线
                    canvas.drawLine(centerX, centerY, coordinate.x, coordinate.y, linePaint);
                    //轴title
                    Coordinate coordinateText = axisTitleList.get(i);
                    canvas.drawText(coordinateText.text, coordinateText.x, coordinateText.y, textPaint);
                }
            }
        }
    }

    //绘制背景网格 / 数据
    private void drawBG(Canvas canvas, List<ItemBean> lineList, boolean canvasPoint) {
        if (lineList != null) {

            //测试数据不纳入其中
            int startPos = 0;
            if (canvasPoint)
                if (itemBeanList.size() > 1) startPos = 1;
            for (int i = startPos; i < lineList.size(); i++) {
                ItemBean itemBean = lineList.get(i);
                linePaint.setColor(itemBean.getColor());
                List<Coordinate> coordinateList = itemBean.getCoordinateList();
                if (coordinateList != null) {
                    for (int j = 0; j < coordinateList.size(); j++) {
                        Coordinate coordinate = coordinateList.get(j);
                        if (j != 0) {
                            Coordinate pre = coordinateList.get(j - 1);
                            canvas.drawLine(pre.x, pre.y, coordinate.x, coordinate.y, linePaint);
                        } else {
                            Coordinate last = coordinateList.get(coordinateList.size() - 1);
                            canvas.drawLine(last.x, last.y, coordinate.x, coordinate.y, linePaint);
                        }
                        if (canvasPoint)
                            canvas.drawCircle(coordinate.x, coordinate.y, 6, linePaint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (showPart) {
                //取消展示详情数据
                selectPos = -1;
                if (onItemSelectListener != null) onItemSelectListener.onItemSelect(-1);
                showPart = false;
                invalidate();
            } else {
                //展示详情数据
                touchX = event.getX();
                touchY = event.getY();
                if (Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2) > Math.pow(maxRadius, 2))
                    return true;
                double angle = CoordinateUtils.getAngle(touchX, touchY, centerX, centerY, transAngle);//0-360
                for (int i = 0; i < axisNum; i++) {
                    int startAngle = (int) (i / (float) axisNum * 360);//0
                    int endAngle = (int) ((i + 1) / (float) axisNum * 360);//360
                    if (startAngle < angle && angle < endAngle) {
                        onSelectPart(i);
                        break;
                    }
                }
            }

        }
        return true;
    }

    private void onSelectPart(int pos) {
        selectPos = pos;
        showPart = true;
        invalidate();
        if (onItemSelectListener != null) onItemSelectListener.onItemSelect(pos);
        //刷新布局 展示部分数据
    }

    public static class ItemBean {
        private String title;
        private int color;
        private List<Entity> dataList;
        private List<Coordinate> coordinateList;

        public ItemBean() {
        }

        public ItemBean(String title, int color, List<Entity> dataList) {
            this.title = title;
            this.color = color;
            this.dataList = dataList;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Entity> getDataList() {
            return dataList;
        }

        public void setDataList(List<Entity> dataList) {
            this.dataList = dataList;
        }

        public List<Coordinate> getCoordinateList() {
            return coordinateList;
        }

        public void setCoordinateList(List<Coordinate> coordinateList) {
            this.coordinateList = coordinateList;
        }
    }

    private OnItemSelectListener onItemSelectListener;

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {
        void onItemSelect(int pos);
    }
}
