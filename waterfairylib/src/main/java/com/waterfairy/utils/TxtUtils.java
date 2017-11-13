package com.waterfairy.utils;

import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by water_fairy on 2017/4/13.
 * ver_1.1:中文/中文符号  2017/11/13
 */

public class TxtUtils {

    public static void writeTxt(File file, String txt, boolean createNew, boolean append) throws IOException {
        boolean canSave = false;
        if (!file.exists()) {
            String parent = file.getParent();
            File file1 = new File(parent);
            canSave = file1.mkdirs();
        } else {
            canSave = true;
        }
        if (canSave) {
            FileWriter fileWriter = new FileWriter(file, append);
            fileWriter.write(txt);
            fileWriter.close();
        }
    }

    public static void writeTxt(File file, String txt) throws IOException {
        writeTxt(file, txt, false, true);
    }

    public static float getTextLen(String content, float textSize) {
        if (android.text.TextUtils.isEmpty(content) || textSize <= 0) return 0;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(content);
    }

    //使用UnicodeScript方法判断
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isChineseByScript(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        if (sc == Character.UnicodeScript.HAN) {
            return true;
        }
        return false;
    }

    //使用UnicodeBlock方法判断
    public static boolean isChineseByBlock(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D;
        } else {
            return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
        }

    }

    // 根据UnicodeBlock方法判断中文标点符号
    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                    || ub == Character.UnicodeBlock.VERTICAL_FORMS;
        } else {
            return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS;
        }

    }
}
