package com.waterfairy.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019-11-15 13:47
 * @info: 主线程中调用
 */
public class LocationTool {
    public static final int ERROR_NO_PERMISSION = 1;//没有权限
    public static final int ERROR_ACTIVITY_IS_EMPTY = 2;//页面已销毁
    public static final int ERROR_NO_PROVIDER_CAN_USE = 3;//没有位置获取途径
    public static final int ERROR_ERROR_LOCATION_DISABLE = 5;//位置定位未打开
    private static final String TAG = "LocationTool";
    private OnGetLocationListener onGetLocationListener;
    private LocationManager locationManager;
    private HashMap<String, LocationListener> listenerHashMap;
    private Activity activity;

    public LocationTool setOnGetLocationListener(OnGetLocationListener onGetLocationListener) {
        this.onGetLocationListener = onGetLocationListener;
        return this;
    }

    public interface OnGetLocationListener {
        void onGetLocation(Location location);

        void onGetLocationError(int errCode, String msg);
    }

    private LocationTool() {

    }

    public static LocationTool newInstance() {
        return new LocationTool();
    }

    /**
     * 定位
     *
     * @param activity
     * @return
     */
    public void location(Activity activity) {
        if (activity != null) {
            this.activity = activity;
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    //请求权限
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10010);
                onGetLocationListener.onGetLocationError(ERROR_NO_PERMISSION, "没有位置权限");
            } else {
                if (locationManager == null)
                    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                if (listenerHashMap == null) {
                    listenerHashMap = new HashMap<>();
                }

                java.util.List<String> allProviders = locationManager.getAllProviders();
                if (allProviders != null && allProviders.size() != 0) {
                    for (String providerTemp : allProviders) {
                        LocationListener updateListener = generateLocationUpdateListener();
                        if (!listenerHashMap.containsKey(providerTemp)) {
                            android.util.Log.i(TAG, "location: requestLocationUpdates " + providerTemp);
                            listenerHashMap.put(providerTemp, updateListener);
                            locationManager.requestLocationUpdates(providerTemp, 500, 0, updateListener);
                        }
                    }
                } else {
                    onGetLocationListener.onGetLocationError(ERROR_NO_PROVIDER_CAN_USE, "位置信息获取途径不可用");
                }
            }
        } else {
            onGetLocationListener.onGetLocationError(ERROR_ACTIVITY_IS_EMPTY, "页面已销毁");
        }
    }


    private LocationListener generateLocationUpdateListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                android.util.Log.i(TAG, "onLocationChanged: " + location.toString());
                if (listenerHashMap != null) {
                    LocationListener locationListener = listenerHashMap.get(location.getProvider());
                    if (locationListener != null) {
                        locationManager.removeUpdates(locationListener);
                        listenerHashMap.remove(location.getProvider());
                        if (onGetLocationListener != null && activity != null) {
                            onGetLocationListener.onGetLocation(location);
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {
                android.util.Log.i(TAG, "onStatusChanged: " + provider + " , " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                android.util.Log.i(TAG, "onProviderEnabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                android.util.Log.i(TAG, "onProviderDisabled: " + provider);
                if (onGetLocationListener != null) {
                    onGetLocationListener.onGetLocationError(ERROR_ERROR_LOCATION_DISABLE, "定位未打开");
                }
            }
        };
    }

    public void release() {
        removeUpdates();
        listenerHashMap = null;
        locationManager = null;
        onGetLocationListener = null;
        activity = null;
    }

    private void removeUpdates() {
        if (listenerHashMap != null && locationManager != null) {
            java.util.Set<String> strings = listenerHashMap.keySet();
            for (String provider : strings) {
                locationManager.removeUpdates(listenerHashMap.get(provider));
            }
            listenerHashMap.clear();
        }
    }

}
