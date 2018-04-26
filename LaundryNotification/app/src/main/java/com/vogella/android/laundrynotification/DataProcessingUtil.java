package com.vogella.android.laundrynotification;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.data.Acceleration;

import java.util.HashMap;

/**
 * This class processes the data sent from the accelerometer
 */
public class DataProcessingUtil {
    private Boolean machineStarted;
    private int currentMin;
    private long currentMinTimeStamp;
    private HashMap<Integer,Integer> frequencyCount;
    private double runningAvg;
    private int dataPointCount;
    private int belowThresholdCount;

    public DataProcessingUtil() {
        this.machineStarted = false;
        this.currentMin = 0;
        this.frequencyCount = new HashMap<Integer,Integer>();
        this.frequencyCount.put(this.currentMin, 0);
        this.currentMinTimeStamp = 0;
        this.runningAvg = 0;
        this.dataPointCount = 0;
        this.belowThresholdCount = 0;
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
                this.frequencyCount.put(this.currentMin, 0);
            }
        }

        Log.i("AppLog", data.value(Acceleration.class).toString());
        double x_value = data.value(Acceleration.class).x();
        double y_value = data.value(Acceleration.class).y();
        double z_value = data.value(Acceleration.class).z();

        double magnitude = Math.sqrt(Math.pow(x_value,2) + Math.pow(y_value,2) + Math.pow(z_value,2));

        if (Math.abs(magnitude - this.runningAvg)>= 1.043) {
            int currentFreq = this.frequencyCount.get(this.currentMin);
            this.frequencyCount.put(this.currentMin, currentFreq + 1);
        }

        this.runningAvg = (magnitude + this.runningAvg)/(this.dataPointCount + 1);
        this.dataPointCount++;
    }

    public void checkMachineStatus() {
        Log.i("Troubleshooting", this.frequencyCount.toString());
        if (this.currentMin >= 1) {
            // Stops running here.  I don't get any of the following Logs.
            if (this.frequencyCount.get(this.currentMin - 1) < 50) {
                this.belowThresholdCount += 1;
            } else {
                this.belowThresholdCount = 0;
            }
            Log.i("Troubleshooting", "Below Threshold: " + this.belowThresholdCount);
            if (this.belowThresholdCount == 10) {
                this.machineStarted = false;
                Log.i("Troubleshooting", "Machine not running");
            } else {
                this.machineStarted = true;
                Log.i("Troubleshooting", "Machine running");
            }
        }
    }

    public Boolean getMachineStarted() {
        return machineStarted;
    }
}
