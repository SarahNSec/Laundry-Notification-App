package com.vogella.android.laundrynotification;

import android.util.Log;

public class NotificationUtil {
    private String iftttAPIKey = "dgVbFkX8p2zJKoMUx3Czyh";
    private String event_hook = "machine_status_change";

    public static void post() {
        // make the post request
        Log.i("AppLog", "Making a post request (almost)");
    }
}
