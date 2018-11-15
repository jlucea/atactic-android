package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class CampaignTargetsRequest {

    private static final String LOG_TAG = "CampaignTargetsRequest";

    /**
     * Get the recommended targets for a given participation id
     *
     * @param userId User's id
     * @param participationId ParticipationSummary for which targets are requested
     * @return JSON block containing the list of targets for the participation
     */
    public static String send(int userId, int participationId){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_QUEST_TARGETS
                + "?uid=" + userId
                + "&pid=" + participationId);

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
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }
}
