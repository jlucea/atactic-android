package io.atactic.android.network.request;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.NetworkUtils;

public class ChangePasswordRequest {

    private static final String LOG_TAG = "ChangePasswordRequest";

    private static final String CHANGE_PASSWORD_RESOURCE = "/profile/pwd";

    public static HttpResponse send(int userId, String newPassword) {

        URL url = NetworkUtils.buildUrl(CHANGE_PASSWORD_RESOURCE);
        Log.d(LOG_TAG, url.toString());

        try {
            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String params = "uid="+userId+"&npwd="+newPassword;

            // Write form parameters
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(params);
            writer.flush();
            writer.close();

            return new HttpResponse(connection.getResponseCode(),connection.getResponseMessage());

        }catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }



}
