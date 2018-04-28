package com.lab.home.laundrynotification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 *  An activity to display the settings fragment
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String MW_MAC_ADDRESS = "00:00:00:00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add the settings fragment to the activity view
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
