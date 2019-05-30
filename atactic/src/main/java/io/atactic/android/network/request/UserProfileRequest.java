package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class UserProfileRequest {

    private static final String LOG_TAG = "UserProfileRequest";

    /**
     * Gets the user profile info from the server
     *
     * @param userId User's id
     * @return JSON Object containing the full profile for the user
     */
    public static HttpResponse send(int userId){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_PROFILE + "?uid=" + userId);
        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            HttpResponse response = new HttpResponse();
            response.setCode(urlConnection.getResponseCode());

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = NetworkUtils.readStreamContent(urlConnection.getInputStream());
                response.setContent(responseContent);
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
