package com.waterfairy.utils;

import android.app.Activity;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by water_fairy on 2017/3/23.
 */

public class ToolBarUtils {
    public static Toolbar initToolBarBack(AppCompatActivity activity, int toolbarId, int titleId, boolean translucentStatus, OnToolBarBackClickListener listener) {
        return initToolBarBack(activity, toolbarId, titleId, translucentStatus, listener, -1);
    }

    public static Toolbar initToolBarBack(AppCompatActivity activity, int toolbarId, int titleId, boolean translucentStatus, OnToolBarBackClickListener listener, int toolBarTextSize) {
        return initToolBarBack(activity, toolbarId, activity.getResources().getString(titleId), translucentStatus, listener, toolBarTextSize);
    }

    public static Toolbar initToolBarBack(AppCompatActivity activity, int toolbarId, String title, boolean translucentStatus, final OnToolBarBackClickListener listener) {
        return initToolBarBack(activity, toolbarId, title, translucentStatus, listener, -1);
    }

    /**
     * @param activity          activity
     * @param toolbarId         toolBar 控件id
     * @param title             title 资源String ID
     * @param translucentStatus 透明状态栏
     * @param listener          返回监听
     * @param toolBarTextSize   labelName 字体大小
     * @return
     */
    public static Toolbar initToolBarBack(AppCompatActivity activity, int toolbarId, String title, boolean translucentStatus, final OnToolBarBackClickListener listener, int toolBarTextSize) {
        Toolbar toolbar = activity.findViewById(toolbarId);
        toolbar.setTitle(title);
        if (toolBarTextSize != -1) {
            TextView tollBarTextView = getTollBarTextView(toolbar);
            if (tollBarTextView != null)
                tollBarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, toolBarTextSize);
        }
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
        if (translucentStatus) {
            setTranslucentStatus(activity, true);
        }

        return toolbar;
    }

    /**
     * @param activity
     * @param drawerLayoutId    布局id
     * @param toolbarId         toolBar id
     * @param titleId           title res id
     * @param transparent       阴影?
     * @param translucentStatus 状态栏沉浸?
     * @return
     */
    public static Toolbar initToolBarMenu(AppCompatActivity activity,
                                          int drawerLayoutId,
                                          int toolbarId,
                                          int titleId,
                                          boolean transparent,
                                          boolean translucentStatus, final OnDrawerLayoutListener onDrawerLayoutListener) {
        return initToolBarMenu(activity, drawerLayoutId, toolbarId, titleId, true, translucentStatus, onDrawerLayoutListener, -1);
    }


    /**
     * @param activity
     * @param drawerLayoutId    布局id
     * @param toolbarId         toolBar id
     * @param titleId           title res id
     * @param transparent       阴影?
     * @param translucentStatus 状态栏沉浸?
     * @return
     */
    public static Toolbar initToolBarMenu(AppCompatActivity activity,
                                          int drawerLayoutId,
                                          int toolbarId,
                                          int titleId,
                                          boolean transparent,
                                          boolean translucentStatus, final OnDrawerLayoutListener onDrawerLayoutListener, int textSize) {
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(drawerLayoutId);
        Toolbar toolbar = (Toolbar) activity.findViewById(toolbarId);
        if (transparent) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(activity.getResources().getDisplayMetrics().density * 3);
            }
        }
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, titleId, titleId) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (onDrawerLayoutListener != null) {
                    onDrawerLayoutListener.onDrawerClosed(drawerView);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (onDrawerLayoutListener != null) {
                    onDrawerLayoutListener.onDrawerOpened(drawerView);
                }
            }
        };
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
        if (titleId == 0) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            toolbar.setTitle(activity.getResources().getString(titleId));
            if (textSize != -1) {
                TextView tollBarTextView = getTollBarTextView(toolbar);
                if (tollBarTextView != null)
                    tollBarTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }
        if (translucentStatus) {
            setTranslucentStatus(activity, true);
        }
        return toolbar;
    }

    public static void setTranslucentStatus(Activity activity, boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    /**
     * toolBar 需要设置 title 才能获取
     *
     * @param toolbar
     * @return
     */
    public static TextView getTollBarTextView(Toolbar toolbar) {
        try {
            Class toolBarClass = Class.forName("android.support.v7.widget.Toolbar");
            Field textViewFiled = toolBarClass.getDeclaredField("mTitleTextView");
            textViewFiled.setAccessible(true);
            return (TextView) textViewFiled.get(toolbar);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnDrawerLayoutListener {
        void onDrawerClosed(View drawerView);

        void onDrawerOpened(View drawerView);
    }

    public interface OnToolBarBackClickListener {
        void onToolBarBackClick();
    }
}
