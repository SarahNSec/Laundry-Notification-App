package com.vogella.android.laundrynotification;

/**
 * Class defines the possible states that the machine can be in
 */

public enum MachineStatus {
    OFF(R.string.status_value_off),
    RUNNING(R.string.status_value_running),
    FINISHED(R.string.status_value_finished),
    UNKNOWN(R.string.status_unknown);

    private int string_id;

    MachineStatus(int string_id) {
        this.string_id = string_id;
    }

    public int getStringID() {
        return this.string_id;
    }
}
