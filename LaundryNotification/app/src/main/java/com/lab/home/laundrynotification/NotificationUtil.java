package com.lab.home.laundrynotification;

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
    private String event_hook = "machine_status_change";
    private OkHttpClient client;

    public NotificationUtil() {
        this.client = new OkHttpClient();
    }

    /**
     * Creates a GET request to the IFTTT API
     * @throws Exception if the connection is unsuccessful.
     */
    public void get() throws Exception {
        // make the get request
        String url = String.format("https://maker.ifttt.com/trigger/%s/with/key/%s", this.event_hook, this.iftttAPIKey);

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
