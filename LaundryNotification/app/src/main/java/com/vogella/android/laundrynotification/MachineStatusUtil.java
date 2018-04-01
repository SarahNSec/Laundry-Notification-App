package com.vogella.android.laundrynotification;


/**
 * Created by sarah on 3/31/2018.
 */

public class MachineStatusUtil {
    private MachineStatus status;

    // Sets the status
    // Accepts the following values: off, running, finished
    public void setStatus(String newStatus) {
        switch (newStatus) {
            case "off":
                this.status = MachineStatus.OFF;
                break;
            case "running":
                this.status = MachineStatus.RUNNING;
                break;
            case "finished":
                this.status = MachineStatus.FINISHED;
                break;
        }
    }

    public int getStatusStringID() {
        return this.status.getStringID();
    }

}
