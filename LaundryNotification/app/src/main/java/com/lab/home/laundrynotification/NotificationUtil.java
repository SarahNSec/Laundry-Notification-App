package com.lab.home.laundrynotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  Facilitates making requests to the If This Then That (IFTTT) API
 *  to create notifications.
 */
public class NotificationUtil {
    private String iftttAPIKey = "dgVbFkX8p2zJKoMUx3Czyh";
    private static final String KEY_API_KEY_1 = "preference_apiKey1";
    private static final String KEY_API_KEY_2 = "preference_apiKey2";
    private static final String KEY_API_KEY_3 = "preference_apiKey3";
    private String event_hook = "machine_status_change";
    private OkHttpClient client;

    public NotificationUtil() {
        this.client = new OkHttpClient();
    }

    /**
     * Creates a GET request to the IFTTT API
     * @throws Exception if the connection is unsuccessful.
     */
    public void get(Context context) throws Exception {
        // Get API Key values from settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String key1 = sharedPref.getString(KEY_API_KEY_1, "");
        String key2 = sharedPref.getString(KEY_API_KEY_2, "");
        String key3 = sharedPref.getString(KEY_API_KEY_3, "");

        // make the get requests
        if (!key1.isEmpty()) {
            String url = String.format("https://maker.ifttt.com/trigger/%s/with/key/%s", this.event_hook, key1);
            this.get_helper(url);
        }
        if (!key2.isEmpty()) {
            String url = String.format("https://maker.ifttt.com/trigger/%s/with/key/%s", this.event_hook, key2);
            this.get_helper(url);
        }
        if (!key3.isEmpty()) {
            String url = String.format("https://maker.ifttt.com/trigger/%s/with/key/%s", this.event_hook, key3);
            this.get_helper(url);
        }
    }

    /**
     * A helper function that actually makes the API GET request
     * @param url the URL to make a GET request to
     */
    private void get_helper(String url) {
        // Make get request
        Log.i("AppLog", "Making a get request to: " + url);
        Request request = new Request.Builder().url(url).build();
        Log.i("AppLog", "request created: " + request.toString());

        this.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("AppLog", "Get request complete: " + response.body().string());
            }
        });
    }
}
