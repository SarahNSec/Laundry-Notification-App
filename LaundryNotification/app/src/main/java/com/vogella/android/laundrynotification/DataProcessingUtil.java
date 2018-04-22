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
    private double runningAvg;
    private int dataPointCount;

    public DataProcessingUtil() {
        this.machineStarted = false;
        this.currentMin = 0;
        this.frequencyCount = new ArrayList<Integer>();
        this.currentMinTimeStamp = 0;
        this.runningAvg = 0;
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

        double magnitude = Math.sqrt(Math.pow(x_value,2) + Math.pow(y_value,2) + Math.pow(z_value,2));

        if (Math.abs(magnitude - this.runningAvg)>= 1.043) {
            Log.i("AppLog", "Significant Event at minute " + this.currentMin);
            this.frequencyCount.set(this.currentMin, this.frequencyCount.get(this.currentMin) + 1);
        }

        Log.i("AppLog", data.value(Acceleration.class).toString());
        this.runningAvg = (magnitude + this.runningAvg)/(this.dataPointCount + 1);
        this.dataPointCount++;

        if (this.currentMin >= 10) {
            this.setMachineStatus();
        }
    }

    private void setMachineStatus() {
        int belowThreshold = 0;
        for (int i=this.currentMin - 10; i <= this.currentMin; i++) {
            if (this.frequencyCount.get(i) < 50) {
                belowThreshold += 1;
            }
        }
        if (belowThreshold == 10) {
            this.machineStarted = false;
        } else {
            this.machineStarted = true;
        }
    }

    public Boolean getMachineStarted() {
        return machineStarted;
    }
}
