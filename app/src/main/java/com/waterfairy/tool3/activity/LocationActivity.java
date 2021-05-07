package com.waterfairy.tool3.activity;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
    private LocationTool locationTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        textView = ((TextView) findViewById(R.id.tv_location));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
        initLocation();
    }

    private void initLocation() {

        locationTool = LocationTool.newInstance();
        locationTool.setOnGetLocationListener(new LocationTool.OnGetLocationListener() {
            @Override
            public void onGetLocation(Location location) {
                if (location != null) {
//                    location
                    final double longitude = location.getLongitude();
                    final double latitude = location.getLatitude();

                    final String info = location.getProvider() + ":" + longitude + " " + latitude;
                    textView.setText(info);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.CHINA);
                                String infoTemp = info;
                                List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 10);
                                if (fromLocation != null && fromLocation.size() > 0) {
                                    for (int i = 0; i < fromLocation.size(); i++) {
                                        Address address = fromLocation.get(i);
                                        infoTemp += ("\n\n" + (i + 1) + "." + address.getLocality() + "-" + address.getSubLocality() + " : \n" + address.toString());
                                    }
                                }
                                final String finalInfoTemp = infoTemp;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(finalInfoTemp);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();

                    Log.i("LocationActivity", "location: " + info);
                } else {
                    Log.i("LocationActivity", "location: " + null);
                }
            }

            @Override
            public void onGetLocationError(int errCode, String msg) {

            }
        });
    }

    public void location(View view) {
        locationTool.location(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTool.release();
    }
}
