package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class ParticipationTargetsRequest {

    private static final String LOG_TAG = "QuestTargetsRequest";

    /**
     * Get the recommended targets for a given participation id
     *
     * @param userId User's id
     * @param participationId Participation for which targets are requested
     * @param usrPosLatitude User's latitude
     * @param usrPosLongitude User's longitude
     * @return JSON block containing the list of targets for the participation
     */
    public static String send(int userId, int participationId,
                                float usrPosLatitude, float usrPosLongitude){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_QUEST_TARGETS
                + "?uid=" + userId
                + "&pid=" + participationId
                + "&usrLat=" + usrPosLatitude
                + "&usrLon=" + usrPosLongitude);

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
