package com.waterfairy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.waterfairy.utils.AssetsUtils;
import com.waterfairy.xmlParser.XmlAttrBean;
import com.waterfairy.xmlParser.XmlNodeBean;
import com.waterfairy.xmlParser.XmlParser;
import com.xueduoduo.jssecurityedu.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/5/23 09:24
 * @info:
 */
public class XmlTextView extends View {
    private static final String TAG = "xmlTextView";
    private XmlNodeBean nodeBean;
    public static final String DEFAULT_COLOR = "#7c7d7e";
    public static final String DEFAULT_TEXT_SIZE = "14";
    public final static String NODE_STRING = "font";
    public final static String NODE_ROOT = "content";
    public final static String COLOR_STRING = "color";
    public final static String TEXT_SIZE_STRING = "textSize";
    private List<LineData> mLineDataList;//绘画所需坐标数据
    private static float density;
    private int LINE_DIVIDER = 0;//间隔
    private Paint mPaint;

    private int maxWidth, maxHeight;

    private boolean measure = true;
    private float textSize;
    private int textColor = -1;


    public XmlTextView(Context context) {
        this(context, null);
    }

    public XmlTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XmlTextView);
//        String string = typedArray.getString(R.styleable.XmlTextView_xmlText);
//        if (!TextUtils.isEmpty(string)) {
//            setText(typedArray.getString(R.styleable.XmlTextView_xmlText), false);
//        }
        try {
            String assetPath = typedArray.getString(R.styleable.XmlTextView_xmlAssetsPath);
            if (!TextUtils.isEmpty(assetPath)) {
                InputStream is = AssetsUtils.getIS(context, assetPath);
                if (is != null)
                    setXmlIS(AssetsUtils.getIS(context, typedArray.getString(R.styleable.XmlTextView_xmlAssetsPath)), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LINE_DIVIDER = (int) typedArray.getDimension(R.styleable.XmlTextView_xmlLineSpace, 0f);
        typedArray.recycle();
        initTest();
    }

    private void initTest() {
        if (nodeBean == null)
            nodeBean = new XmlNodeBean();
    }

    public void setText(String text) {
        setText(text, true);
    }

    public void setTextColor(int color) {
        textColor = color;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setText(String text, boolean fresh) {
        setXmlNode(new XmlNodeBean(NODE_ROOT, "").addNodeBean(new XmlNodeBean(NODE_STRING, text)), fresh);
    }

    public void setXmlText(String xmlText) {
        setXmlIS(getIS(xmlText));
    }

    public void setXmlIS(InputStream inputStream) {
        setXmlIS(inputStream, true);
    }

    public void setXmlIS(InputStream inputStream, boolean fresh) {
        try {
            setXmlNode(XmlParser.getPullHashMapParser().readXml(inputStream));
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
            String xmlText = "";
            try {
                xmlText = getStringFromIs(inputStream);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setXmlNode(new XmlNodeBean(NODE_STRING, xmlText), fresh);
            Log.e(TAG, "read xmlText Error or it's null,check the text form");
        }
    }

    private String getStringFromIs(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";
        String xmlText = "";
        byte[] bytes = new byte[1024 * 50];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1) {
            xmlText += new String(bytes, 0, len);
        }
        return xmlText;
    }

    int lastWidth = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (measure) {
            calcLineData(getPaddingLeft(), sizeWidth - getPaddingRight());
            measure = false;
        }
        if (mLineDataList != null && mLineDataList.size() > 0) {
            LineData lineData = mLineDataList.get(mLineDataList.size() - 1);
            int viewHeight = lineData.y + lineData.height + getPaddingBottom() + lineData.height / 5;
            setMeasuredDimension(sizeWidth, viewHeight);
        }

//        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//        if (sizeWidth != 0 && ((lastWidth != sizeWidth) || measure)) {
//            calcLineData(getPaddingLeft(), sizeWidth - getPaddingRight());
//            if (mLineDataList != null && mLineDataList.size() > 0) {
//                LineData lineData = mLineDataList.get(mLineDataList.size() - 1);
//                int viewHeight = lineData.y + lineData.height + getPaddingBottom() + lineData.height / 5;
//                setMeasuredDimension(sizeWidth, viewHeight);
//            }
//            measure = false;
//        }
//        lastWidth = sizeWidth;
    }


    public void setXmlNode(XmlNodeBean nodeBean) {
        setXmlNode(nodeBean, true);
    }

    private void setXmlNode(XmlNodeBean nodeBean, boolean fresh) {
        this.nodeBean = nodeBean;
        measure = true;
        if (fresh)
            invalidate();
    }

    private InputStream getIS(String xmlText) {
        if (TextUtils.isEmpty(xmlText)) {
            return null;
        }
        return new ByteArrayInputStream(xmlText.getBytes());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int startX = 0;
        int endX = width;
        int startY = 0;
        int endY = height;
        if (endX - startX <= 0 || endY - startY <= 0) {
            return;
        }
        //绘制文本
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (mLineDataList != null)
            for (int i = 0; i < mLineDataList.size(); i++) {
                LineData lineData = mLineDataList.get(i);
                List<LineData.TextData> textDataList = lineData.getTextDataList();
                for (int j = 0; j < textDataList.size(); j++) {
                    LineData.TextData textData = textDataList.get(j);
                    mPaint.setColor(textColor != -1 ? textColor : textData.textColor);
                    mPaint.setTextSize(textSize != 0 ? textSize : textData.textSize);
                    canvas.drawText(textData.text, textData.startX, lineData.y + lineData.height, mPaint);
//                    canvas.drawLine(textData.startX, lineData.y, textData.endX, lineData.y, mPaint);
                }
            }
    }

    private void calcLineData(int startX, int endX) {
        int maxWidth = endX - startX;
        //取root 中的第一层节点
        List<XmlNodeBean> nodeBeans = nodeBean.getNodeBeans();
        //每次绘制一行 确保高度
        mLineDataList = new ArrayList<>();
        LineData tempLineData = new LineData();
        tempLineData.y = LINE_DIVIDER + getPaddingTop();
        for (int i = 0; i < nodeBeans.size(); i++) {
            XmlNodeBean xmlNodeBean = nodeBeans.get(i);
            String text = xmlNodeBean.getNodeValue();
            if (!TextUtils.isEmpty(text)) {
                LineData.TextData textData = getLineTextData(xmlNodeBean);
                int minPaddingRight = 0;
                String preText = "";
                int preWidth = 0;
                int startPos = 0;
                //每段文字 开始循环计算长度
                for (int j = 0; j < text.length(); j++) {
                    String willText = text.substring(startPos, j + 1);
                    //计算每段字符
                    Rect willRect = getTextRect(willText, textSize != 0 ? textSize : textData.textSize);
                    int willAddWidth = willRect.right + (willRect.left > 0 ? willRect.left : (-willRect.left));
                    int willAddHeight = willRect.bottom + (willRect.top > 0 ? willRect.top : (-willRect.top));
                    //增加之后的长度
                    if (tempLineData.width + willAddWidth + minPaddingRight > maxWidth) {
                        //增加一个字符后超过最大宽 - 准备换行
                        if (preText.length() >= 1) {
                            //增加之前的文本
                            //1.去除将添加的文本后的长度大于等于1 (还有文本)
                            //前段文字
                            LineData.TextData tempTextData = new LineData.TextData();
                            tempTextData.text = preText;
                            tempTextData.textColor = textData.textColor;
                            tempTextData.textSize = textSize != 0 ? textSize : textData.textSize;
                            tempTextData.startX = tempLineData.width + startX;
                            tempTextData.endX = preWidth + tempLineData.width + startX;
                            tempLineData.getTextDataList().add(tempTextData);
                        }
                        j--;//退一个字符
                        startPos = j + 1;
                        //添加一行
                        mLineDataList.add(tempLineData);
                        maxWidth = Math.max(tempLineData.width, maxWidth);
                        //重建一行
                        int nextY = tempLineData.y + tempLineData.height;
                        tempLineData = new LineData();
                        tempLineData.y = nextY + LINE_DIVIDER;
                        //换行
                    } else {
                        //行改变
                        tempLineData.height = Math.max(tempLineData.height, willAddHeight);
                        //不换
                        if (j == text.length() - 1) {
                            //后段文字
                            LineData.TextData tempTextData = new LineData.TextData();
                            tempTextData.text = new String(willText);
                            tempTextData.textColor = textData.textColor;
                            tempTextData.textSize = textSize != 0 ? textSize : textData.textSize;
                            tempTextData.startX = tempLineData.width + startX;
                            tempTextData.endX = willAddWidth + tempLineData.width + startX;

                            tempLineData.getTextDataList().add(tempTextData);
                            tempLineData.width += (willAddWidth);//间隔
                        }
                    }
                    preText = willText;
                    preWidth = willAddWidth;
                }
            }
            if (i == nodeBeans.size() - 1) {
                //最后一个 添加进数据
                maxWidth = Math.max(tempLineData.width, maxWidth);
                mLineDataList.add(tempLineData);
            }
        }
    }

    private LineData.TextData getLineTextData(XmlNodeBean xmlNodeBean) {
        LineData.TextData textData = new LineData.TextData();
        List<XmlAttrBean> attrBeans = xmlNodeBean.getAttrBeans();
        for (int j = 0; j < attrBeans.size(); j++) {
            XmlAttrBean xmlAttrBean = attrBeans.get(j);
            String attrValue = xmlAttrBean.getAttrValue();
            switch (xmlAttrBean.getAttrName()) {
                case COLOR_STRING:
                    try {
                        textData.textColor = Color.parseColor(attrValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case TEXT_SIZE_STRING:
                    try {
                        textData.textSize = Integer.parseInt(attrValue) * density;
                    } catch (Exception e) {
                    }
                    break;
            }
        }
        return textData;
    }


    static class LineData {
        int linNum;//第一行  0 , 1 , 2 ...
        int width;//宽度
        int y;//Y 坐标
        int height;//高度
        List<TextData> textDataList;

        public List<TextData> getTextDataList() {
            if (textDataList == null) return textDataList = new ArrayList<>();
            return textDataList;
        }

        public void setTextDataList(List<TextData> textDataList) {
            this.textDataList = textDataList;
        }

        static class TextData {
            int startX;//X 坐标
            int endX;//X 坐标
            int textColor = Color.parseColor(DEFAULT_COLOR);
            float textSize = Integer.parseInt(DEFAULT_TEXT_SIZE) * density;
            String text;
        }
    }

    public Rect getTextRect(String content, float textSize) {
        Rect rect = new Rect();
        if (TextUtils.isEmpty(content) || textSize <= 0) return rect;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect;
    }


}
