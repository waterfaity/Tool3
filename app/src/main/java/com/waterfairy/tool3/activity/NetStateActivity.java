package com.waterfairy.tool3.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.waterfairy.netState.NetUtils;
import com.waterfairy.tool3.R;

public class NetStateActivity extends AppCompatActivity {
    private TextView mTVState;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_state);
        mTVState = (TextView) findViewById(R.id.text);
        NetUtils.getNetworkType(this);
        mTVState.setText(NetUtils.isNetworkAvailable(this) + "");
          broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    mTVState.setText(NetUtils.isNetworkAvailable(NetStateActivity.this) + " -- new ");
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
