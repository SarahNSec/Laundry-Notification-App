package com.vogella.android.laundrynotification;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Accelerometer;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private BtleService.LocalBinder serviceBinder;
    private final String MW_MAC_ADDRESS= "DA:62:2D:9A:D5:8D";
    private MetaWearBoard board;
    private Accelerometer accelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the Metawear Btle service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);
        Log.i("AppLog", "onCreate method called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unbind the Metawear Btle service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Typecast the binder to the Metawear Btle service's LocalBinder class
        serviceBinder = (BtleService.LocalBinder) service;

        Log.i("AppLog", "Service Connected");

        this.retrieveBoard(this.MW_MAC_ADDRESS);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // Disconnect from the board
        this.disconnectBoard(this.MW_MAC_ADDRESS);
    }

    // connect to the Metawear board device
    private void retrieveBoard(String macAddr) {
        final BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice = btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // create the MetaWear board object
        this.board = serviceBinder.getMetaWearBoard(remoteDevice);
        // connect to the board over bluetooth
        this.board.connectAsync().continueWith(new Continuation<Void, Void>() {

            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    Log.i("AppLog", "Failed to connect");
                } else {
                    Log.i("AppLog", "Connected");
                }
                return null;
            }
        });
    }

    // Disconnects from the Metawear board
    private void disconnectBoard(String macAddr) {
        this.board.disconnectAsync().continueWith(new Continuation<Void, Void>() {

            @Override
            public Void then(Task<Void> task) throws Exception {
                Log.i("AppLog", "Disconnected");
                return null;
            }
        });
    }
}
