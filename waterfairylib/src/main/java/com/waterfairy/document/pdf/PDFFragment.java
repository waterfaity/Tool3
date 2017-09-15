package com.waterfairy.document.pdf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.waterfairy.utils.MD5Utils;

import java.io.File;

/**
 * Created by water_fairy on 2017/7/6.
 * 995637517@qq.com
 */

public class PDFFragment extends Fragment implements OnPPTLoadListener, OnLoadCompleteListener {
    private PDFView pdfView;
    private String pdfPath;
    private OnOfficeLoadListener onOfficeLoadListener;
    private String sdPath;

    public void setPDFPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public void loadPPT(String pptPath) {
        setPDFPath(pptPath);
        initPath();
        initPDFData();
    }

    private void initPath() {
        sdPath = getActivity().getExternalCacheDir() + "/pdf/" + MD5Utils.getMD5Code(pdfPath);
        File file = new File(sdPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                sdPath = null;
            }
        }
    }

    public static PDFFragment newInstance() {
        return new PDFFragment();
    }

    public void initPDFData() {
//        pdfView.setOnPPTLoadingListener(this);
//        pdfView.setWidth(getResources().getDisplayMetrics().widthPixels);
//        if (onOfficeLoadListener != null)
//            onOfficeLoadListener.onLoading("");
//        pdfView.loadPPT(getActivity(), pdfPath);

        if (onOfficeLoadListener != null)
            onOfficeLoadListener.onLoading("");
        pdfView.fromFile(new File(pdfPath))   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical(true)  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true)   //是否允许翻页，默认是允许翻
                .onLoad(this)
                // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
                .load();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String pptPath = savedInstanceState.getString("pdfPath");
            if (!TextUtils.isEmpty(pptPath)) {
                this.pdfPath = pptPath;
            }
        }
        pdfView = new PDFView(getActivity(), null);
        initPath();
        initPDFData();
        return pdfView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("pdfPath", pdfPath);
        super.onSaveInstanceState(outState);
    }

    public void setOnLoadingListener(OnOfficeLoadListener onOfficeLoadListener) {
        this.onOfficeLoadListener = onOfficeLoadListener;
    }

    @Override
    public void onLoadSuccess() {
        if (onOfficeLoadListener != null)
            onOfficeLoadListener.onLoadSuccess();
    }

    @Override
    public void onLoadError() {
        if (onOfficeLoadListener != null)
            onOfficeLoadListener.onLoadError();
    }

    @Override
    public void onLoading(final String content) {
        if (onOfficeLoadListener != null) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onOfficeLoadListener.onLoading(content);
                    }
                });
            }
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        if (onOfficeLoadListener != null) {
            onOfficeLoadListener.onLoadSuccess();
        }
    }
}
