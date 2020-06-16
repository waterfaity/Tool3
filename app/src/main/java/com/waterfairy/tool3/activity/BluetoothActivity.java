package com.waterfairy.tool3.activity;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.waterfairy.bluetooth.ble.BLEManager;
import com.waterfairy.tool3.R;

import java.util.Set;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "bluetooth";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


//        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//        ComponentName componentName = new ComponentName(getPackageName(), MediaButtonBroadcastReceiver.class.getName());
//        audioManager.registerMediaButtonEventReceiver(componentName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaSession fff = new MediaSession(this, "fff");
            Intent intent = new Intent();
            intent.setAction("jj.jj.jj.jj.");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1001, intent, PendingIntent.FLAG_ONE_SHOT);
            fff.setMediaButtonReceiver(pendingIntent);
        }


        print();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void print() {

        BLEManager bleManager = BLEManager.getInstance();
        boolean b = bleManager.initBL(this);
        BluetoothManager bluetoothManager = bleManager.getBluetoothManager();
        Set<BluetoothDevice> bondedDevices = bluetoothManager.getAdapter().getBondedDevices();
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            Log.i(TAG, "onCreate: " + bluetoothDevice.getAddress() + "\t" + bluetoothDevice.getName());
        }
    }
}