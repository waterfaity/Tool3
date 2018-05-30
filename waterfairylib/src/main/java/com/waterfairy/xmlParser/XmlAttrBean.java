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

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    @Override
    public String toString() {
        return " " + attrName + " = '" + attrValue + "'";
    }
}
