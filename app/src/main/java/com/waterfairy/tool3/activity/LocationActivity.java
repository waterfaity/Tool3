package com.waterfairy.tool3.activity;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.waterfairy.tool3.R;
import com.waterfairy.location.LocationTool;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        textView = ((TextView) findViewById(R.id.tv_location));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    public void location(View view) {
//
//
//        BaiDuLocationTool baiDuLocationTool = BaiDuLocationTool.newInstance();
//        baiDuLocationTool.initLocationOption(getApplicationContext());
//        baiDuLocationTool.setLocationListener(new BDAbstractLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation bdLocation) {
////此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
////以下只列举部分获取经纬度相关（常用）的结果信息
////更多结果信息获取说明，请参照类参考中BDLocation类中的说明
////获取纬度信息
//                double latitude = bdLocation.getLatitude();
////获取经度信息
//                double longitude = bdLocation.getLongitude();
////获取定位精度，默认值为0.0f
//                float radius = bdLocation.getRadius();
////获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//                String coorType = bdLocation.getCoorType();
//
//                if (TextUtils.equals(coorType, "gcj02")) {
//                    double[] doubles = GpsTransUtils.gcj02_To_Bd09(latitude, longitude);
//                    latitude=doubles[0];
//                    longitude=doubles[1];
//
//                }
//
////获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//                int errorCode = bdLocation.getLocType();
//
//                String info = longitude + "," + latitude + "-" + radius + "-" + coorType + " - " + errorCode;
//
//                TextView textView = findViewById(R.id.text);
//                textView.setText(info);
//                Log.i("test", "onReceiveLocation: " + info);
//            }
//        });
//        baiDuLocationTool.location();
//

        LocationTool locationTool = LocationTool.newInstance();
        locationTool.setOnGetLocationListener(new LocationTool.OnGetLocationListener() {
            @Override
            public void onGetLocation(Location location) {
                if (location != null) {
//                    location
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    String info = location.getProvider() + ":" + longitude + " " + latitude;

                    Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.CHINA);
                    try {
                        List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 10);
                        if (fromLocation != null && fromLocation.size() > 0) {

                            for (int i = 0; i < fromLocation.size(); i++) {
                                Address address = fromLocation.get(i);
                                info += ("\n\n" + (i + 1) + "." + address.getLocality() + "-" + address.getSubLocality() + " : \n" + address.toString());

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


//                    Geocoder
                    textView.setText(info);
                    Log.i("LocationActivity", "location: " + info);
                } else {
                    Log.i("LocationActivity", "location: " + null);
                }
            }

            @Override
            public void onGetLocationError(int errCode, String msg) {

            }
        });
        locationTool.location(this);

    }
}
