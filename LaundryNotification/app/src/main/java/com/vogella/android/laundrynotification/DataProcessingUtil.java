package com.vogella.android.laundrynotification;

import android.util.Log;

import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.data.Acceleration;

public class DataProcessingUtil {
    private Boolean machineStarted;

    public DataProcessingUtil() {
        this.machineStarted = false;
    }

    public void processData(Data data) {
        // process the data
        Log.i("AppLog", data.value(Acceleration.class).toString());
        data.value(Acceleration.class);
    }
}
