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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bolts.Continuation;
import bolts.Task;

/**
 * Main Activity for the app.  Establishes layout and creates and connects to MetaWear
 * board object.  Also establishes a timer to regularly check the machine status.
 */
public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private BtleService.LocalBinder serviceBinder;
    private final String MW_MAC_ADDRESS= "F7:02:E6:49:04:AF";
    //private final String MW_MAC_ADDRESS= "C7:CF:3D:0E:D9:0E";
    private MetaWearBoard board;
    private MachineStatus machineStatus;
    private NotificationUtil notifications;
    private Accelerometer accelerometer;
    private DataProcessingUtil dataproc;
    private ScheduledExecutorService scheduleTaskExecutor;
    private int followupNotificationTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        // Set the machine status to Off and update the view to display that status
        this.setMachineStatusValue(MachineStatus.OFF);

        // establish notification utility & follow-up timer
        this.notifications = new NotificationUtil();
        this.followupNotificationTimer = 0;

        // establish data processing utility
        this.dataproc = new DataProcessingUtil();

        // Bind the Metawear Btle service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        // Create task scheduler to check machine status every 60 seconds
        this.scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        this.scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Every 60 seconds, check the machine status
                dataproc.checkMachineStatus();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI
                        MachineStatus newStatus = determineMachineStatus(dataproc.getMachineStarted());
                        Log.i("Troubleshooting", "machine status: " + newStatus);
                        setMachineStatusValue(newStatus);
                    }
                });
            }
        },0, 60, TimeUnit.SECONDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_settings:
                // User selected the settings menu item
                // TODO: Open the settings UI
                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
            default:
                // The users action was not recognized so call super class
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        Log.i("AppLog", "On destroy called");
        super.onDestroy();
        Log.i("AppLog", "Super.onDestroy called");
        this.disconnectBoard(this.MW_MAC_ADDRESS);
        Log.i("AppLog", "Disconnect board called");

        // Unbind the Metawear Btle service when the activity is destroyed
        getApplicationContext().unbindService(this);
        Log.i("AppLog", "After service destroyed");
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
        Log.i("AppLog", "On service disconnected called");
        this.disconnectBoard(this.MW_MAC_ADDRESS);
        Log.i("AppLog", "Disconnect board called successfully");
    }

    /**
     * Connect the app to the metawear board device and start the accelerometer
     */
    private void retrieveBoard(String macAddr) {
        Log.i("AppLog", "MAC Addr: " + macAddr);

        // retrieve the Bluetooth device
        final BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice = btManager.getAdapter().getRemoteDevice(macAddr);

        // create the MetaWear board object
        this.board = serviceBinder.getMetaWearBoard(remoteDevice);

        // connect to the board over bluetooth
        this.board.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {

            @Override
            public Task<Route> then(Task<Void> task) throws Exception {
                Log.i("AppLog", "Connected to " + macAddr);

                // configure the accelerometer and connect
                accelerometer = board.getModule(Accelerometer.class);
                accelerometer.configure().odr(25f).commit();
                return accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                // Process the data each time a point is received
                                dataproc.processData(data);
                            }
                        });
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                if (task.isFaulted()) {
                    Log.w("AppLog", "Failed to configure app", task.getError());
                } else {
                    Log.i("AppLog", "App configured");

                    // once configured, start the accelerometer
                    accelerometer.acceleration().start();
                    accelerometer.start();
                    Log.i("AppLog", "Accelerometer started");
                }
                return null;
            }
        });
    }

    /**
     * Function that is called when one of the buttons for changing status is called.
     * This will be transitioned once the data is driving the status change.
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.statusOff_button:
                // change the status to OFF
                Log.i("AppLog", "Changing status to OFF");
                this.setMachineStatusValue(MachineStatus.OFF);
                accelerometer.stop();
                accelerometer.acceleration().stop();
                break;
            case R.id.statusRunning_button:
                // change the status to running
                Log.i("AppLog", "Changing status to RUNNING");
                this.setMachineStatusValue(MachineStatus.RUNNING);
                accelerometer.acceleration().start();
                accelerometer.start();
                break;
            case R.id.statusFinished_button:
                // change the status to finished
                Log.i("AppLog", "Changing status to FINISHED");
                this.setMachineStatusValue(MachineStatus.FINISHED);
                try {
                    this.notifications.get();
                } catch (Exception e) {
                    Log.w("AppLog", "ERROR_MAIN_THREAD");
                }
                break;
            default:
                // do nothing
                Log.i("AppLog", "ERROR: did not recognize action");
                break;
        }
    }

    /**
     *  Disconnects the app from the Metawear board and turns off the accelerometer
     */
    private void disconnectBoard(String macAddr) {
        accelerometer.stop();
        accelerometer.acceleration().stop();
        this.board.disconnectAsync().continueWith(new Continuation<Void, Void>() {

            @Override
            public Void then(Task<Void> task) throws Exception {
                Log.i("AppLog", "Disconnected");
                return null;
            }
        });
    }

    /**
     * Sets the machine status variable to the new value and updates the screen to reflect that
     * variable
     */
    private void setMachineStatusValue(MachineStatus status) {
        this.machineStatus = status;
        Log.i("AppLog", "New Machine Status: " + this.machineStatus);
        TextView statusText = (TextView)findViewById(R.id.statusValue);
        statusText.setText(this.machineStatus.getStringID());
    }

    /**
     * This method determines the current machine status based on the existing status and
     * the feedback from the data processing utility.
     * Returns the new status that is determined (or the existing status if nothing changed)
     */
    private MachineStatus determineMachineStatus(Boolean machineRunning) {
        MachineStatus currStatus = this.machineStatus;
        if (currStatus == MachineStatus.OFF && machineRunning) {
            // Machine was off, but now is running; switch to running
            return MachineStatus.RUNNING;
        } else if (currStatus == MachineStatus.RUNNING && !machineRunning) {
            // Machine was running, but now is not; send notification and switch to finished
            try {
                this.notifications.get();
            } catch (Exception e) {
                Log.w("AppLog", "Unable to send notification: " + e);
            }
            return MachineStatus.FINISHED;
        } else if (currStatus == MachineStatus.FINISHED && !machineRunning) {
            // Machine is finished and still needs to be unloaded; increase follow-up timer
            this.followupNotificationTimer += 1;
            if (this.followupNotificationTimer == 60) {
                // it has been 60 minutes since the machine stopped, send a follow-up notification
                try {
                    this.notifications.get();
                } catch (Exception e) {
                    Log.w("AppLog", "Unable to send notification: " + e);
                }
            }
            // return current status because nothing has changed
            return this.machineStatus;
        } else if (currStatus == MachineStatus.FINISHED && machineRunning){
            // machine has been unloaded.  Switch to off and reset followup timer
            this.followupNotificationTimer = 0;
            return MachineStatus.OFF;
        } else {
            // if anything else happens (i.e. nothing changed) return the current status
            return this.machineStatus;
        }
    }
}
