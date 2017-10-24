package com.waterfairy.apkLoader;

/**
 * Created by Administrator on 2017/8/23.
 */

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

/**
 * 加载补丁dex文件，并插入系统dex数组中的第一个位置
 * <p>
 * Created by June on 2017/7/14.
 */

public class PatchDexLoader {

    private Context mContext;

    public PatchDexLoader(Context context) {
        this.mContext = context;
    }

    public void load(String path) {
        try {
            // 已加载的dex
            Object dexPathList = getField(BaseDexClassLoader.class, "pathList", mContext.getClassLoader());
            Object dexElements = getField(dexPathList.getClass(), "dexElements", dexPathList);

            // patchdex
            String dexOptDir = mContext.getDir("patchDex_optDir", 0).getAbsolutePath();
            DexClassLoader dcl = new DexClassLoader(path, dexOptDir, null, mContext.getClassLoader());
            Object patchDexPathList = getField(BaseDexClassLoader.class, "pathList", dcl);
            Object patchDexElements = getField(patchDexPathList.getClass(), "dexElements", patchDexPathList);

            // 将patchdex和已加载的dexes数组拼接连接
            Object concatDexElements = concatArray(patchDexElements, dexElements);

            // 重新给dexPathList#dexElements赋值
            setField(dexPathList.getClass(), "dexElements", dexPathList, concatDexElements);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param cls       被访问对象的class
     * @param fieldName 对象的成员变量名
     * @param object    被访问对象
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public Object getField(Class<?> cls, String fieldName, Object object) throws NoSuchFieldException, IllegalAccessException {
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }


    /**
     * @param cls       被访问对象的class
     * @param fieldName 对象的成员变量名
     * @param object    被访问对象
     * @param value     赋值给成员变量
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void setField(Class<?> cls, String fieldName, Object object, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }


    /**
     * 连接两个数组（指定位置）
     *
     * @param left  连接后在新数组的左侧
     * @param right 连接后在新数组的右侧
     * @return
     */
    public Object concatArray(Object left, Object right) {
        int len1 = Array.getLength(left);
        int len2 = Array.getLength(right);
        int totalLen = len1 + len2;
        Object concatArray = Array.newInstance(left.getClass().getComponentType(), totalLen);
        for(int i = 0; i < len1; i++) {
            Array.set(concatArray, i, Array.get(left, i));
        }
        for(int j = 0; j < len2; j++) {
            Array.set(concatArray, len1 + j, Array.get(right, j));
        }
        return concatArray;
    }
}