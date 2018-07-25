package com.waterfairy.xmlParser;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/5/23 09:42
 * @info:
 */
public class XmlAttrBean {
    private String attrName = "";
    private String attrValue = "";

    public XmlAttrBean() {
    }

    public XmlAttrBean(String attrName, String attrValue) {
        this.attrName = attrName;
        this.attrValue = attrValue;
    }

    public String getAttrName() {
        return attrName;
    }

    public XmlAttrBean setAttrName(String attrName) {
        this.attrName = attrName;
        return this;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public XmlAttrBean setAttrValue(String attrValue) {
        this.attrValue = attrValue;
        return this;
    }

    @Override
    public String toString() {
        return " " + attrName + " = '" + attrValue + "'";
    }
}
