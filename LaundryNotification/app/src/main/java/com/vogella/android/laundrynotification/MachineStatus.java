package com.vogella.android.laundrynotification;

/**
 * Created by sarah on 4/1/2018.
 */

public enum MachineStatus {
    OFF(R.string.status_value_off),
    RUNNING(R.string.status_value_running),
    FINISHED(R.string.status_value_finished);

    private int string_id;

    MachineStatus(int string_id) {
        this.string_id = string_id;
    }

    public int getStringID() {
        return this.string_id;
    }
}
