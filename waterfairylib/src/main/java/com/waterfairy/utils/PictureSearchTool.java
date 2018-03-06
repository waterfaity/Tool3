package com.waterfairy.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * user : water_fairy
 * email:995637517@qq.com
 * date :2017/12/6
 * des  : 原作用于搜索图片 ,修改extension  可以搜索指定的文件
 * des  : 如果该文件夹A存在符合条件的文件AB 记录该文件夹A和文件AB ,然后跳过该文件夹A搜索下一个文件夹
 * des  : 未知:file.listFile()  文件夹在前?
 */

public class PictureSearchTool {
    private static final String TAG = "pictureSearchTool";
    private List<ImgBean> fileList = new ArrayList<>();
    //    private String extension[] = new String[]{".png", ".jpg", ".jpeg", ".PNG", ".JPEG", ".JPG"};
    private String extension[] = new String[]{".png", ".jpg"};
    private boolean running;
    private int deep = 1;
    private static final PictureSearchTool PICTURE_SEARCH_TOOL = new PictureSearchTool();
    private OnSearchListener onSearchListener;


    private PictureSearchTool() {

    }

    public static PictureSearchTool getInstance() {
        return PICTURE_SEARCH_TOOL;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public void setExtension(String... extension) {
        this.extension = extension;
    }


    public void start() {
        if (extension == null || extension.length == 0) {
            if (onSearchListener != null) {
                onSearchListener.onSearchError("请设置搜索文件类型!");
            }
        } else if (deep <= 0) {
            if (onSearchListener != null) {
                onSearchListener.onSearchError("搜索文件层次必须大于等于1!");
            }
        } else {
            running = true;
            fileList.removeAll(fileList);
            startAsyncTask();
        }
    }

    private void startAsyncTask() {
        new AsyncTask<Void, String, List<ImgBean>>() {

            @Override
            protected List<ImgBean> doInBackground(Void... voids) {
                //搜索外置sd卡
                search(Environment.getExternalStorageDirectory(), 0, new OnSearchListener() {
                    @Override
                    public void onSearch(String path) {
                        publishProgress(path);
                    }

                    @Override
                    public void onSearchSuccess(List<ImgBean> fileList) {

                    }

                    @Override
                    public void onSearchError(String message) {

                    }
                });
                return fileList;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (values != null && onSearchListener != null) {
                    if (values.length == 1) {
                        onSearchListener.onSearch(values[0]);
                    } else if (values.length == 2) {
                        //暂未有该情况
                        onSearchListener.onSearchError(values[1]);
                    }
                }
            }

            @Override
            protected void onPostExecute(List<ImgBean> strings) {
                if (onSearchListener != null) onSearchListener.onSearchSuccess(strings);
            }
        }.execute();
    }

    public void stop() {
        running = false;
    }


    private void search(File file, int deep, OnSearchListener onSearchListener) {
        Log.i(TAG, "search: " + deep);
        //文件夹存在 并在deep范围内
        if (file.exists() && deep < this.deep) {
            File[] list = file.listFiles();
            if (list != null) {
                //作为一个搜索限制 如果该文件夹A存在符合条件的文件AB 记录该文件夹A和文件AB ,然后跳过该文件夹A搜索下一个文件夹
                boolean jump = false;
                //遍历该文件夹下的所有文件及文件夹
                for (File childFile : list) {
                    if (childFile.isDirectory()) {
                        //是文件夹->继续扫描下一级文件夹
                        search(childFile, deep + 1, onSearchListener);
                    } else if (!jump) {
                        //是文件 并且不跳过
                        String childAbsolutePath = childFile.getAbsolutePath();
                        for (String anExtension : extension) {
                            if (childAbsolutePath.endsWith(anExtension)) {
                                fileList.add(new ImgBean(file.getAbsolutePath(), childAbsolutePath));
                                jump = true;
                                break;
                            }
                        }
                        if (jump) {
                            //如果有跳过 说明搜索到了符合条件的文件
                            if (onSearchListener != null)
                                onSearchListener.onSearch(childAbsolutePath);
                        }
                    }
                }
            }
        }
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    /**
     * 搜索指定文件夹的所有符合条件的文件
     *
     * @param path
     * @return
     */
    public List<ImgBean> searchFolder(String path) {
        List<ImgBean> imgBeans = new ArrayList<>();
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String childPath = file.getAbsolutePath();
                for (String anExtension : extension) {
                    if (childPath.endsWith(anExtension)) {
                        imgBeans.add(new ImgBean(null, childPath, true));
                    }
                }
            }
        }
        Log.i(TAG, "searchFolder: " + imgBeans.size());
        return imgBeans;
    }

    public interface OnSearchListener {
        void onSearch(String path);

        void onSearchSuccess(List<ImgBean> fileList);

        void onSearchError(String message);
    }

    public class ImgBean {
        public ImgBean(String path, String firstImgPath) {
            this.path = path;
            this.firstImgPath = firstImgPath;
        }

        public ImgBean(String path, String firstImgPath, boolean isImg) {
            this.path = path;
            this.firstImgPath = firstImgPath;
            this.isImg = isImg;
        }

        private boolean open;
        private int pos;
        private boolean isImg;//是否是指定的文件类型
        private String path;
        private String firstImgPath;

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFirstImgPath() {
            return firstImgPath;
        }

        public boolean isImg() {
            return isImg;
        }

        public void setImg(boolean img) {
            isImg = img;
        }

        public void setFirstImgPath(String firstImgPath) {

            this.firstImgPath = firstImgPath;
        }
    }

    public boolean isRunning() {
        return isRunning();
    }
}
