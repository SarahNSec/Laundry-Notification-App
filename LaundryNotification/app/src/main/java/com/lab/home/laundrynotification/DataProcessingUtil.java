package com.lab.home.laundrynotification;

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

    /**
     * Function to process each data point as it comes in.
     * @param data the data point to process
     */
    public void processData(Data data) {
        // process the data

        // first get the current timestamp (which is based on epoch time - epoch time when the app started
        if (this.currentMinTimeStamp == 0) {
            this.currentMinTimeStamp = System.currentTimeMillis()/1000/60;
        } else {
            long currentEpoch = System.currentTimeMillis()/1000/60;
            // check if we have rolled over to the next minute; if so, increment currentMin
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

        // calculate the vector magnitude (sqrt(x^2 + y^2 + z^2))
        double magnitude = Math.sqrt(Math.pow(x_value,2) + Math.pow(y_value,2) + Math.pow(z_value,2));

        // compare the vector magnitude to the running average.  If it's greater than this threshold,
        // then it is considered a significant event and it is logged in currentFreq for the current minute.
        // Significant events are logged per minute.
        if (Math.abs(magnitude - this.runningAvg)>= 1.043) {
            int currentFreq = this.frequencyCount.get(this.currentMin);
            this.frequencyCount.put(this.currentMin, currentFreq + 1);
        }

        // add the current point to the running average
        this.runningAvg = (magnitude + this.runningAvg)/(this.dataPointCount + 1);
        this.dataPointCount++;
    }

    /**
     * Determines whether the machine is running or off based on the processed data
     */
    public void checkMachineStatus() {
        Log.i("AppLog", this.frequencyCount.toString());
        // We always check the last minute, so make sure at least a minute has passed
        if (this.currentMin >= 1) {
            if (this.frequencyCount.get(this.currentMin - 1) < 50) {
                // if the previous minute has fewer significant events than the threshold (50), then the
                // machine is in an "off state" so increment counter
                this.belowThresholdCount += 1;
            } else {
                // else, reset the counter to 0 because the machine was "running" in that minute
                // and we only want to switch the status to a non-running status if the machine has
                // been off for 10 minutes or more
                this.belowThresholdCount = 0;
            }
            if (this.belowThresholdCount == 10) {
                // if the counter has hit 10, the machine has been off for 10 minutes and is officially
                // considered not running
                this.machineStarted = false;
                Log.i("AppLog", "Machine not running");
                // Decrease the counter so that if it's still not running in the next minute, the counter
                // hits 10 again and the machine stays off
                this.belowThresholdCount -= 1;
            } else {
                // if the machine has less than 10 minutes, it's still running
                this.machineStarted = true;
                Log.i("Troubleshooting", "Machine running");
            }
        }
    }

    /**
     * Gets the machine status
     * @return machine's status (on or off)
     */
    public Boolean getMachineStarted() {
        return machineStarted;
    }
}
