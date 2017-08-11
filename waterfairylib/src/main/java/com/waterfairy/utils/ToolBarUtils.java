package com.waterfairy.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by water_fairy on 2017/3/23.
 */

public class ToolBarUtils {
    public static Toolbar initToolBarBack(Context context, int toolbarId, int titleId, final OnToolBarBackClickListener listener) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Toolbar toolbar = (Toolbar) activity.findViewById(toolbarId);
        toolbar.setTitle(context.getResources().getString(titleId));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onToolBarBackClick();
                }
            }
        });
        return toolbar;
    }

    /**
     * @param activity
     * @param drawerLayoutId 布局id
     * @param toolbarId      toolBar id
     * @param titleId        title res id
     * @param transparent    透明?
     * @return
     */
    public static Toolbar initToolBarMenu(Activity activity, int drawerLayoutId, int toolbarId, int titleId, boolean transparent) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
        DrawerLayout drawerLayout = (DrawerLayout) appCompatActivity.findViewById(drawerLayoutId);
        Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(toolbarId);
        if (transparent) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(0);
            }
        }
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(appCompatActivity, drawerLayout, toolbar, titleId, titleId);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
        if (titleId == 0) {
            appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        return toolbar;
    }

    public static interface OnToolBarBackClickListener {
        void onToolBarBackClick();
    }
}
