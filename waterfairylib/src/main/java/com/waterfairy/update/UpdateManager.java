package com.waterfairy.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.waterfairy.okhttp.callback.NormalCallback;
import com.waterfairy.okhttp.callback.ProgressCallback;
import com.waterfairy.okhttp.manager.OkHttpManager;
import com.waterfairy.xmlParser.PullHashMapParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by water_fairy on 2016/12/21.
 */

public class UpdateManager {

    public static final int STATE_NO_UPDATE = 0;//没有更新
    public static final int STATE_CAN_UPDATE = 1;//可以更新
    public static final int STATE_MUST_UPDATE = 2;//强制更新

    private static final String IGNORE = "ignore";

    private OkHttpManager okHttpManager;
    private OnUpdateCallback onUpdateCallback;
    private final String SERVER_URL = "http://version.hyanglao.com:56000";
    private String saveName, key;//保存名字(com.waterfairy.tool) 关键字 (tool)
    private Context context;
    private boolean isHandleCheck;//手动检查更新
    private boolean isVersionIgnore;//该版本有没有被忽略

    private UpdateManager() {

    }

    private static final UpdateManager UPDATE_MANAGER = new UpdateManager();

    public static UpdateManager getInstance() {
        return UPDATE_MANAGER;
    }

    /**
     * 获取当前程序版本
     *
     * @param context
     * @return
     */
    private String getLocalVer(Activity context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public UpdateManager checkVersion(Activity activity, String key) {
        return checkVersion(activity, key, false);
    }

    /**
     * 检查版本信息及处理
     * 后续:
     * 1.新版本提示
     * (2.提示安装本地安装包)
     * 3.网络下载
     * 4.安装
     *
     * @param context
     * @param key
     */
    public UpdateManager checkVersion(final Activity context, String key, boolean handleCheck) {
        this.context = context;
        this.key = key;
        this.isHandleCheck = handleCheck;
        isVersionIgnore = false;
        checkVersion(context, key, new VerCallback() {
            @Override
            public void getState(int state, String link, final String text) {
                switch (state) {
                    case STATE_NO_UPDATE:
                        //不做处理
                        if (onUpdateCallback != null) {
                            onUpdateCallback.onUpdate(false, isVersionIgnore);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(text)) {
                                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (isHandleCheck) {
                                            Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                        break;
                    case STATE_CAN_UPDATE:
                        //提示更新,可以忽略
                        showUpdate(context, false, link, text);
                        if (onUpdateCallback != null) {
                            onUpdateCallback.onUpdate(true, isVersionIgnore);
                        }
                        break;
                    case STATE_MUST_UPDATE:
                        //强制更新,不允许返回
                        showUpdate(context, true, link, text);
                        if (onUpdateCallback != null) {
                            onUpdateCallback.onUpdate(true, isVersionIgnore);
                        }
                        break;
                }
            }
        });
        return this;
    }

    /**
     * 保存名字  默认app包名
     * 如:(com.waterfairy.tool)
     * -1.0.apk
     *
     * @param saveName
     * @return
     */
    public UpdateManager setSaveName(String saveName) {
        this.saveName = saveName;
        return this;
    }

    /**
     * 提示更新
     *
     * @param context
     * @param mustUpdate
     * @param link
     * @param text
     */
    private void showUpdate(final Activity context, boolean mustUpdate, final String link, String text) {
        if (checkIgnore(link)) return;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(!mustUpdate);
        alertDialog.setTitle("发现新版本");
        alertDialog.setMessage(text + (mustUpdate ? "" : "\n\n是否更新?"));
        alertDialog.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                checkLocalFile(context, saveName, link);
            }
        });
        if (!mustUpdate) {
            alertDialog.setNegativeButton("忽略该版本", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveIgnoreVersion(link);
                }
            });
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }

    private String getSavePath() {
        return context.getExternalCacheDir().getAbsolutePath() + "/apk";
    }

    private String getIgnoreFilePath() {
        return getSavePath() + "/" + IGNORE + ".xml";
    }

    private InputStream getIgnoreFile() throws IOException {
        String ignoreFilePath = getIgnoreFilePath();
        File file = new File(ignoreFilePath);
        if (!file.exists()) return null;
        return new FileInputStream(file);
    }

    private HashMap<String, String> getPullHashMap() throws IOException, XmlPullParserException {
        return getPullHashMapParser().readXml(IGNORE, getIgnoreFile());
    }

    private PullHashMapParser getPullHashMapParser() {
        return PullHashMapParser.getPullHashMapParser();
    }

    private boolean checkIgnore(String link) {
        if (isHandleCheck) {
            isVersionIgnore = false;
        } else {
            try {
                HashMap<String, String> hashMap = getPullHashMap();
                String ignoreVersion = hashMap.get(key);
                isVersionIgnore = !TextUtils.isEmpty(ignoreVersion) && ignoreVersion.equals(getVersionFromLink(link) + "");
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
                isVersionIgnore = false;
            }
        }
        return isVersionIgnore;
    }

    private float getVersionFromLink(String link) {
        if (!TextUtils.isEmpty(link)) {
            if (link.length() > 4) {
                link = link.substring(0, link.length() - 4);
                String[] split = link.split("-");
                if (split.length == 2) {
                    String versionStr = split[1];
                    float version = 0;
                    try {
                        version = Float.parseFloat(versionStr);
                    } catch (NumberFormatException ignored) {
                    }
                    return version;
                }
            }
        }
        return 0;
    }


    /**
     * 获取网络 版本
     *
     * @param link
     */
    private void saveIgnoreVersion(String link) {

        if (!TextUtils.isEmpty(link)) {
            if (link.length() > 4) {
                link = link.substring(0, link.length() - 4);
                String[] split = link.split("-");
                if (split.length == 2) {
                    String versionStr = split[1];
                    try {
                        PullHashMapParser pullHashMapParser = PullHashMapParser.getPullHashMapParser();
                        HashMap<String, String> pullHashMap = getPullHashMap();
                        pullHashMap.put(key, versionStr);
                        pullHashMapParser.writeXml(IGNORE, pullHashMap, getIgnoreFilePath());
                    } catch (NumberFormatException | XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 判断本地是否有安装包
     *
     * @param context
     * @param link
     */
    private void checkLocalFile(final Activity context, String saveName, final String link) {
        String localVersion = "";
        String version = getVersionFromLink(link) + "";
        saveName = TextUtils.isEmpty(saveName) ? context.getPackageName() : saveName;
        final String saveFileApk = getSavePath() + "/" + saveName + "-" + version + ".apk";
        saveName = null;
        File file = new File(saveFileApk);
        if (file.exists()) {
            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(saveFileApk, PackageManager.GET_ACTIVITIES);
            String packageVer = packageInfo.versionName;
            localVersion = packageVer;
            if (!link.contains("-" + packageVer + ".apk")) {
                downloadVer(context, saveFileApk, link);
                return;
            }
        } else {
            downloadVer(context, saveFileApk, link);
            return;
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("发现本地安装包");
        String size = ((int) (file.length() / 10000)) / 100F + "";
        alertDialog.setMessage("版本号:" + localVersion + "\n" + "大　小:" + size + "M\n\n是否安装?");
        alertDialog.setPositiveButton("安装", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                installApk(context, saveFileApk);
            }
        });
        alertDialog.setNegativeButton("继续下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadVer(context, saveFileApk, link);
            }
        });
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }

    /**
     * 网络下载安装包
     *
     * @param context
     * @param link
     */
    private void downloadVer(final Activity context, final String saveFileApk, String link) {
        String savePath = getSavePath();
        File file = new File(savePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "创建保存路径失败,请检查权限", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }
        //下载dialog
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setTitle("下载中");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
        progressDialog.show();

        okHttpManager.downLoadFile(link, saveFileApk, new ProgressCallback() {
            @Override
            public void onFailure(Request request, IOException e) {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(long l, long l1, boolean b) {
                Log.i(TAG, "onProgress: " + l1);
                progressDialog.setProgress((int) (l / (float) l1 * 100));
                if (b) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setTitle("下载完成");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            installApk(context, saveFileApk);
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }

    /**
     * 安装
     *
     * @param context
     * @param saveFileApk
     */
    private void installApk(final Activity context, String saveFileApk) {

        File file = new File(saveFileApk);
        if (!file.exists()) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "安装文件不存在", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(saveFileApk)),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
            context.finish();
        }

    }

    /**
     * 网络请求版本信息
     *
     * @param context
     * @param key
     * @param callback
     */
    private void checkVersion(final Activity context, String key, final VerCallback callback) {
        okHttpManager = OkHttpManager.initInstance();
        final String localVer = getLocalVer(context);
        String url = SERVER_URL + "/version?id=" + key + "&version=" + localVer;
        okHttpManager.get(url, new NormalCallback() {
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    String json = response.body().string();
                    Log.i(TAG, "onResponse: " + json);
                    if ("{\"update\":\"error\"}".equals(json)) {
                        callback.getState(STATE_NO_UPDATE, null, "key值错误");
                        return;
                    }
                    UpdateBean updateBean = new Gson().fromJson(json, UpdateBean.class);
                    operateUpdateBean(updateBean, localVer, callback);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "onFailure: 网络错误");
            }
        });
    }

    /**
     * 网络获取的版本信息数据处理
     *
     * @param updateBean
     * @param localVer
     * @param callback
     */
    private void operateUpdateBean(UpdateBean updateBean, String localVer, VerCallback callback) {
        if (updateBean.isUpdate()) {
            //可以更新,
            boolean mustUpdate = false;
            String compatibleVersion = updateBean.getCompatible_version();
            localVer = localVer.replace(".", "-");
            compatibleVersion = compatibleVersion.replace(".", "-");
            String[] compatibleVersions = compatibleVersion.split("-");
            String[] localVersions = localVer.split("-");
            for (int i = 0; i < 2; i++) {
                String netTemp = compatibleVersions[i];
                String localTemp = localVersions[i];
                int netInt = 0;
                int localInt = 0;
                if (!TextUtils.isEmpty(netTemp)) {
                    netInt = Integer.parseInt(netTemp);//
                } else {
                    netInt = 0;
                }
                if (!TextUtils.isEmpty(localTemp)) {
                    localInt = Integer.parseInt(localTemp);
                } else {
                    localInt = 0;
                }
                if (netInt > localInt) {
                    mustUpdate = true;
                    break;
                }
            }
            if (mustUpdate) {
                //强制更新
                callback.getState(STATE_MUST_UPDATE, updateBean.getLink(), updateBean.getText());
            } else {
                //可以忽略的更新
                callback.getState(STATE_CAN_UPDATE, updateBean.getLink(), updateBean.getText());
            }
        } else {
            //没有更新
            callback.getState(STATE_NO_UPDATE, null, null);
        }
    }

    interface VerCallback {
        void getState(int state, String link, String text);

    }

    private class UpdateBean {

        private String compatible_version;//最低版本要求
        private String link;//下载连接
        private boolean update;//是否有更新
        private String text;//更新说明

        public void setCompatible_version(String compatible_version) {
            this.compatible_version = compatible_version;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        public void setText(String text) {
            this.text = text;
        }

        String getCompatible_version() {
            return compatible_version;
        }

        String getLink() {
            return link;
        }

        public boolean isUpdate() {
            return update;
        }

        public String getText() {
            return text;
        }
    }

    public interface OnUpdateCallback {
        void onUpdate(boolean update, boolean ignore);
    }

    public UpdateManager setOnUpdateCallback(OnUpdateCallback onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
        return this;
    }
}
