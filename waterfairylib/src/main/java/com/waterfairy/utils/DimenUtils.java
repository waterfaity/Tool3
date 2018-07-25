package com.waterfairy.utils;

import com.waterfairy.xmlParser.XmlAttrBean;
import com.waterfairy.xmlParser.XmlNodeBean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/19 10:15
 * @info:
 */
public class DimenUtils {

    public static void outFileNormal() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            values.add(i + 1);
        }
        values.add(35);
        values.add(40);
        values.add(45);
        values.add(50);
        values.add(55);
        values.add(60);
        values.add(65);
        values.add(70);
        values.add(75);
        values.add(80);
        values.add(85);
        values.add(90);
        values.add(95);
        values.add(100);
        values.add(105);
        values.add(110);
        values.add(115);
        values.add(120);
        values.add(130);
        values.add(140);
        values.add(150);
        values.add(160);
        values.add(170);
        values.add(180);
        values.add(190);
        values.add(200);
        values.add(250);
        values.add(300);
        values.add(350);
        values.add(360);
        values.add(400);
        values.add(450);
        values.add(500);
        values.add(550);
        values.add(600);
        values.add(630);
        values.add(640);
        values.add(650);
        values.add(700);
        values.add(750);
        values.add(800);
        values.add(850);
        values.add(900);
        values.add(950);
        values.add(1000);
        float[] timesValues = new float[]{1, 1.1F, 1.2F, 1.25F, 1.3F, 1.4F, 1.5F, 1.6F, 1.75F, 2.0F, 2.25F, 2.5F, 2.75F, 3F, 3.25F, 3.5F};
        for (float timesValue : timesValues) {
            outFile((int) (timesValue * 360), true, values);
            outFile((int) (timesValue * 360 * (16 / 9F)), false, values);
        }
        ToastUtils.show("完成");
    }

    public static void outFile(int maxDp, boolean isPort, List<Integer> values) {
        String data = getData(maxDp / (isPort ? 360F : 640F), values, isPort);
        File file = new File("/sdcard/dimens/values-w" + maxDp + "dp-" + (isPort ? "port" : "land") + "/dimens.xml");
        try {
            TxtUtils.writeTxt(file, "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + data, true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getData(float times, List<Integer> values, boolean isPort) {
        XmlNodeBean xmlNodeBean = new XmlNodeBean();
        xmlNodeBean.setNodeName("resources");
        List<XmlNodeBean> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            list.add(getBean(times, values.get(i), isPort));
        }
        xmlNodeBean.setNodeBeans(list);
        String s = xmlNodeBean.toString();
        System.out.print(s);
        return s;
    }

    private static XmlNodeBean getBean(float times, int value, boolean isPort) {
        if (times <= 0) times = 1;
        int tempValue = Math.round(value * times);
        if (tempValue <= 0) {
            tempValue = 1;
        }
        return new XmlNodeBean("dimen", tempValue + "dp").addAttrBean(new XmlAttrBean("name", "dp" + value));
    }

}
