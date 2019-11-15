package com.waterfairy.tool3.activity;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.waterfairy.tool3.R;
import com.waterfairy.utils.LocationTool;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }

    public void location(View view) {

        LocationTool locationTool = LocationTool.newInstance();
        locationTool.setOnGetLocationListener(new LocationTool.OnGetLocationListener() {
            @Override
            public void onGetLocation(Location location) {
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    ((TextView) findViewById(R.id.tv_location)).setText(location.getProvider() + ":" + longitude + " " + latitude);
                    Log.i("LocationActivity", "location: " + longitude + "  " + latitude);
                } else {
                    Log.i("LocationActivity", "location: " + null);
                }
            }

            @Override
            public void onGetLocationError(int errCode) {

            }
        });
        locationTool.location(this);

    }
}
