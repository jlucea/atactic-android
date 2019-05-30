package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.NetworkUtils;

public class ActivityHistoryRequest {

    private static final String LOG_TAG = "ActivityHistoryRequest";

    private static final String ACTIVITY_REGISTER_RESOURCE = "/activity/list";

    private static final String PARAM_USERID = "uid";

    public static HttpResponse send(int userId){

        URL url = NetworkUtils.buildUrl(ACTIVITY_REGISTER_RESOURCE
                + "?" + PARAM_USERID  + "=" + userId);

        Log.d(LOG_TAG, url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            HttpResponse response = new HttpResponse();
            response.setCode(urlConnection.getResponseCode());

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String content = NetworkUtils.readStreamContent(urlConnection.getInputStream());
                response.setContent(content);
            }

            return response;

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }
    

}
