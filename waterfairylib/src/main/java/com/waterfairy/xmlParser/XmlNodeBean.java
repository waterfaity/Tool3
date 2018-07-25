package com.waterfairy.xmlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/5/23 09:41
 * @info:
 */
public class XmlNodeBean {
    private int level;
    private String nodeName = "";
    private String nodeValue = "";
    private List<XmlAttrBean> attrBeans;
    private List<XmlNodeBean> nodeBeans;

    public XmlNodeBean() {
    }

    public XmlNodeBean(String nodeName, String nodeValue) {
        this.nodeName = nodeName;
        this.nodeValue = nodeValue;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<XmlAttrBean> getAttrBeans() {
        if (attrBeans == null) return attrBeans = new ArrayList<>();
        return attrBeans;

    }

    public XmlNodeBean setAttrBeans(List<XmlAttrBean> attrBeans) {
        this.attrBeans = attrBeans;
        return this;
    }


    public XmlNodeBean addAttrBeans(List<XmlAttrBean> attrBeans) {
        if (attrBeans != null) {
            if (this.attrBeans == null) attrBeans = new ArrayList<>();
            this.attrBeans.addAll(attrBeans);
        }
        return this;
    }

    public XmlNodeBean addAttrBean(XmlAttrBean xmlAttrBean) {
        if (xmlAttrBean != null) {
            if (this.attrBeans == null) attrBeans = new ArrayList<>();
            attrBeans.add(xmlAttrBean);
        }
        return this;
    }

    public List<XmlNodeBean> getNodeBeans() {
        if (nodeBeans == null) return nodeBeans = new ArrayList<>();
        return nodeBeans;
    }

    public XmlNodeBean setNodeBeans(List<XmlNodeBean> nodeBeans) {
        this.nodeBeans = nodeBeans;
        return this;
    }

    public XmlNodeBean addNodeBeans(List<XmlNodeBean> nodeBeans) {
        if (nodeBeans != null) {
            if (this.nodeBeans == null) this.nodeBeans = new ArrayList<>();
            this.nodeBeans.addAll(nodeBeans);
        }
        return this;
    }

    public XmlNodeBean addNodeBean(XmlNodeBean xmlNodeBean) {
        if (xmlNodeBean != null) {
            if (this.nodeBeans == null) nodeBeans = new ArrayList<>();
            nodeBeans.add(xmlNodeBean);
        }
        return this;
    }


    @Override
    public String toString() {
        String arrtString = "";
        for (int i = 0; i < getAttrBeans().size(); i++) {
            arrtString += attrBeans.get(i).toString();
        }
        String nodeString = "";
        for (int i = 0; i < getNodeBeans().size(); i++) {
            nodeString += nodeBeans.get(i).toString();
        }


        return "<" + nodeName + arrtString + ">" + nodeString + nodeValue + "</" + nodeName + ">";
    }
}
