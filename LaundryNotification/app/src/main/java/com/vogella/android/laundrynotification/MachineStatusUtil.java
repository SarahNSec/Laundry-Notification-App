package com.vogella.android.laundrynotification;

import android.content.Context;


/**
 * Created by sarah on 3/31/2018.
 */

public class MachineStatusUtil {
    private String status;

    // Sets the status
    // Accepts the following values: off, running, finished
    public void setStatus(String newStatus) {
        switch (newStatus) {
            case "off":
                //this.status = R.string.status_value_off;
                break;
            case "running":
                //this.status = R.string.status_value_running;
                break;
            case "finished":
                //this.status = R.string.status_value_finished;
                break;
        }
    }

    public String getStatus() {
        return this.status;
    }

}
