package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class QuestListRequest {

    private static final String LOG_TAG = "QuestListRequest";

    /**
     * Request list of quests for a given user
     *
     * @param userId User's ID
     * @return String containing the list of campaigns in JSON format
     */
    public static String send(int userId){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_QUESTS + "?uid=" + userId);

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
