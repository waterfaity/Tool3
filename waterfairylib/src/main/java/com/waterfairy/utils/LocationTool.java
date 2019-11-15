package com.waterfairy.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.UFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019-11-15 13:47
 * @info:
 */
public class LocationTool {


    public static final int ERROR_NO_PERMISSION = 1;
    public static final int ERROR_ACTIVITY_IS_EMPTY = 2;
    public static final int ERROR_NO_PROVIDER_CAN_USE = 3;
    private static final String TAG = "LocationTool";
    private OnGetLocationListener onGetLocationListener;
    private LocationManager locationManager;
    private LocationListener listener;
    private LocationListener noListener;
    private Activity activity;

    public LocationTool setOnGetLocationListener(OnGetLocationListener onGetLocationListener) {
        this.onGetLocationListener = onGetLocationListener;
        return this;
    }

    public interface OnGetLocationListener {
        void onGetLocation(Location location);

        void onGetLocationError(int errCode);
    }

    private LocationTool() {

    }

    public static LocationTool newInstance() {
        return new LocationTool();
    }

    public void location(Activity activity) {
        if (activity != null) {
            this.activity = activity;
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    //请求权限
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10010);
                onGetLocationListener.onGetLocationError(ERROR_NO_PERMISSION);
            } else {
                if (locationManager == null)
                    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                if (listener != null && locationManager != null) {
                    locationManager.removeUpdates(listener);
                }

                if (locationManager != null) {
                    String provider = locationManager.getBestProvider(getCriteria(), true);

                    if (!TextUtils.isEmpty(provider)) {
                        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                        if (lastKnownLocation != null) {
                            if (onGetLocationListener != null) {
                                onGetLocationListener.onGetLocation(lastKnownLocation);
                            }
                            locationManager.requestLocationUpdates(provider, 1000, 0, noListener = getNoListener());
                        } else {
                            locationManager.requestLocationUpdates(provider, 1000, 0, listener = getLocationUpdateListener());
                        }
                    } else {
                        onGetLocationListener.onGetLocationError(ERROR_NO_PROVIDER_CAN_USE);
                    }
                }
            }
        } else {
            onGetLocationListener.onGetLocationError(ERROR_ACTIVITY_IS_EMPTY);
        }
    }

    private LocationListener getNoListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (noListener != null) {
                    locationManager.removeUpdates(noListener);
                    noListener = null;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i(TAG, "onStatusChanged: ");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(TAG, "onProviderEnabled: `");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(TAG, "onProviderDisabled: ");
            }
        };
    }

    public void release() {
        if (locationManager != null) {
            if (noListener != null) {
                locationManager.removeUpdates(noListener);
            }
            if (listener != null) {
                locationManager.removeUpdates(listener);
            }
            locationManager = null;
            activity = null;
        }
    }

    private LocationListener getLocationUpdateListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (onGetLocationListener != null && activity != null) {
                    onGetLocationListener.onGetLocation(location);
                }
                if (listener != null) {
                    locationManager.removeUpdates(listener);
                    listener = null;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i(TAG, "onStatusChanged: ");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(TAG, "onProviderEnabled: `");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(TAG, "onProviderDisabled: ");
            }
        };
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return criteria;
    }
}
