package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class AccountListRequest {

    private static final String LOG_TAG = "AccountListRequest";

    private static final String PARAM_USERID = "uid";

    /**
     * Returns the full Map of Accounts and Targets for the user.
     *
     * @param userId User's ID
     * @return JSON block containing the Map of Accounts and Targets
     */
    public static String send(int userId){
        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_ACCOUNTS
                + "?" + PARAM_USERID + "=" + userId);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                return NetworkUtils.readStreamContent(urlConnection.getInputStream());
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }

}
