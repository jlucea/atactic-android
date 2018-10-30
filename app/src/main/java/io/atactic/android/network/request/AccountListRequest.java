package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class AccountListRequest {

    private static final String LOG_TAG = "AccountListRequest";

    /**
     * Returns OFF-TARGET accounts only.
     *
     * @param userId
     * @return JSON block
     */
    public static String send(int userId){
        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_ACCOUNTS
                + "?uid=" + userId
                + "&offtgtonly=true" );

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = NetworkUtils.readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

}
