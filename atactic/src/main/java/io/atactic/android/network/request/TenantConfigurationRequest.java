package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.NetworkUtils;

public class TenantConfigurationRequest {

    private static final String LOG_TAG = "TenantConfigRequest";
    private static final String CONFIG_RESOURCE = "/user/config";

    public static HttpResponse send(int userId) {

        URL url = NetworkUtils.buildUrl(CONFIG_RESOURCE + "?uid=" + userId);

        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            String responseContent = NetworkUtils.readStreamContent(urlConnection.getInputStream());
            return new HttpResponse(urlConnection.getResponseCode(), responseContent);

        } catch (IOException ioe) {
            Log.e(LOG_TAG, ioe.getMessage());
            return null;
        }
    }


}
