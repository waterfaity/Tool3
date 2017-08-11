package com.waterfairy.xmlParser;


import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by water_fairy on 2017/2/16.
 */

public class PullHashMapParser {
    private final String UTF_8 = "UTF-8";

    private HashMap<String, String> hashMap;

    public static final PullHashMapParser PULL_HASH_MAP_PARSER = new PullHashMapParser();

    public static PullHashMapParser getPullHashMapParser() {
        return PULL_HASH_MAP_PARSER;
    }

    /**
     * 读取xml
     * 适用于 一个root 节点
     * <root>
     * <key1>value1</key1>
     * <key2>value2</key2>
     * <key3>value3</key3>
     * </root>
     *
     * @param root        根目录
     * @param inputStream 输入xml文件流
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    public HashMap<String, String> readXml(String root, InputStream inputStream) throws IOException, XmlPullParserException {
        if (inputStream == null) return new HashMap<>();
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(inputStream, UTF_8);
        int eventCode = xmlPullParser.getEventType();

        String key = null;
        String value = null;
        while (eventCode != XmlPullParser.END_DOCUMENT) {
            switch (eventCode) {
                case XmlPullParser.START_DOCUMENT:
                    hashMap = new HashMap<>();
                    break;
                case XmlPullParser.START_TAG:
                    key = xmlPullParser.getName();
                    if (!key.equals(root)) {
                        value = xmlPullParser.nextText();
                        hashMap.put(key, value);
                    }
                    break;
            }
            eventCode = xmlPullParser.next();
        }
        return hashMap;
    }

    /**
     * 写入xml
     *
     * @param root         根目录
     * @param hashMap      数据(key,value)
     * @param outputStream 输出xml文件流
     * @throws IOException
     */
    public void writeXml(String root, HashMap<String, String> hashMap, OutputStream outputStream) throws IOException {
        if (hashMap != null && outputStream != null) {
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(outputStream, UTF_8);
            xmlSerializer.startDocument(UTF_8, true);
            xmlSerializer.startTag(null, root);
            for (String key : hashMap.keySet()) {
                String value = hashMap.get(key);
                xmlSerializer.startTag(null, key);
                xmlSerializer.text(value);
                xmlSerializer.endTag(null, key);
            }
            xmlSerializer.endTag(null, root);
            xmlSerializer.endDocument();
            outputStream.flush();
            outputStream.close();
        }
    }

    public void writeXml(String root, HashMap<String, String> hashMap, String path) {
        writeXml(root, hashMap, new File(path));

    }

    public void writeXml(String root, HashMap<String, String> hashMap, File file) {
        if (!file.exists()) {
            File parent = file.getParentFile();
            OutputStream outputStream = null;
            boolean hasPath = false;
            hasPath = parent.exists() || parent.mkdirs();
            if (hasPath) {
                try {
                    if (file.createNewFile()) {
                        outputStream = new FileOutputStream(file);
                        writeXml(root, hashMap, outputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("xml parser", "writeXml: create parent  error");
                }
            } else {
                Log.i("xml parser", "writeXml: create parent file error");
            }
        }
    }
}
