package com.waterfairy.xmlParser;


import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by water_fairy on 2017/2/16.
 */

public class XmlParser {
    private final String UTF_8 = "UTF-8";
    public static final XmlParser PULL_HASH_MAP_PARSER = new XmlParser();

    public static XmlParser getPullHashMapParser() {
        return PULL_HASH_MAP_PARSER;
    }

    public XmlNodeBean readXml(InputStream inputStream) throws IOException, XmlPullParserException {
        if (inputStream == null) return null;
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(inputStream, UTF_8);
        XmlNodeBean xmlNodeBean = readNode(xmlPullParser, 0);
        inputStream.close();
        return xmlNodeBean;

    }

    /**
     * @param xmlPullParser
     * @param level         ==0  root
     */
    private XmlNodeBean readNode(XmlPullParser xmlPullParser, int level) throws XmlPullParserException, IOException {
        XmlNodeBean nodeBean = new XmlNodeBean();
        nodeBean.setLevel(level);
        level++;
        int eventCode = xmlPullParser.getEventType();
        while (eventCode != XmlPullParser.END_DOCUMENT) {
            switch (eventCode) {
                case XmlPullParser.START_TAG:
                    //标签开始
                    String key = xmlPullParser.getName();
                    nodeBean.setNodeName(key);
                    //1.读取到属性
                    int attributeCount = xmlPullParser.getAttributeCount();
                    for (int i = 0; i < attributeCount; i++) {
                        String attributeName = xmlPullParser.getAttributeName(i);
                        String attributeValue = xmlPullParser.getAttributeValue(i);
                        nodeBean.getAttrBeans().add(new XmlAttrBean(attributeName, attributeValue));
                    }
                    //读取 tag/文本
                    eventCode = xmlPullParser.next();
                    //2-1.读取到string
                    String textBefore = xmlPullParser.getText();
                    if (!TextUtils.isEmpty(textBefore)) {
                        nodeBean.setNodeValue(xmlPullParser.getText());
                    }
                    //2-2.读取到子tag
                    while (xmlPullParser.START_TAG == (eventCode = xmlPullParser.next())) {
                        //子节点
                        XmlNodeBean childChild = readNode(xmlPullParser, level);
                        nodeBean.getNodeBeans().add(childChild);
                    }
                    String textAfter = xmlPullParser.getText();
                    if (!TextUtils.isEmpty(textAfter)) {
                        nodeBean.setNodeValue(xmlPullParser.getText());
                    }
                    continue;
                case XmlPullParser.END_TAG:
                    //标签结束  返回一个节点
                    xmlPullParser.next();
                    return nodeBean;

            }
            eventCode = xmlPullParser.next();
        }
        return nodeBean;
    }
}