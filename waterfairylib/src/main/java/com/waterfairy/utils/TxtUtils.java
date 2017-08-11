package com.waterfairy.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by water_fairy on 2017/4/13.
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
}
