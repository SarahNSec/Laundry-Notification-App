package com.vogella.android.laundrynotification;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.data.Acceleration;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This class processes the data sent from the accelerometer
 * To start, it assumes that the accelerometer values start at
 * x ~ 0, y ~ 0, and z ~ 1
 */
public class DataProcessingUtil {
    private Boolean machineStarted;
    private int currentMin;
    private long currentMinTimeStamp;
    private ArrayList<Integer> frequencyCount;
    private double runningAvg_x;
    private double runningAvg_y;
    private double runningAvg_z;
    private int dataPointCount;

    public DataProcessingUtil() {
        this.machineStarted = false;
        this.currentMin = 0;
        this.frequencyCount = new ArrayList<Integer>();
        this.currentMinTimeStamp = 0;
        this.runningAvg_x = 0;
        this.runningAvg_y = 0;
        this.runningAvg_z = 0;
        this.dataPointCount = 0;
    }

    public void processData(Data data) {
        // process the data
        if (this.currentMinTimeStamp == 0) {
            this.currentMinTimeStamp = System.currentTimeMillis()/1000/60;
        } else {
            long currentEpoch = System.currentTimeMillis()/1000/60;
            if (currentEpoch != this.currentMinTimeStamp) {
                this.currentMin += (int)(currentEpoch - this.currentMinTimeStamp);
                this.currentMinTimeStamp = currentEpoch;
            }
        }

        double x_value = data.value(Acceleration.class).x();
        double y_value = data.value(Acceleration.class).y();
        double z_value = data.value(Acceleration.class).z();

        if (Math.abs(x_value - this.runningAvg_x)>= 0.005 ||
                Math.abs(y_value - this.runningAvg_y)>= 0.004 ||
                Math.abs(z_value - this.runningAvg_z)>= 0.004) {
            this.frequencyCount.set(this.currentMin, this.frequencyCount.get(this.currentMin) + 1);
        }

        Log.i("AppLog", data.value(Acceleration.class).toString());
        this.runningAvg_x = (x_value + this.runningAvg_x)/(this.dataPointCount + 1);
        this.runningAvg_y = (y_value + this.runningAvg_y)/(this.dataPointCount + 1);
        this.runningAvg_z = (z_value + this.runningAvg_z)/(this.dataPointCount + 1);
        this.dataPointCount++;
    }
}
