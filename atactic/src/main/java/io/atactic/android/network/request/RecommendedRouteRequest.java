package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class RecommendedRouteRequest {

    private static final String LOG_TAG = "RouteRequest";

    /**
     *
     * @param userId
     * @param usrLat
     * @param usrLon
     * @param waypoints
     * @return
     */
    public static String send(int userId, float usrLat, float usrLon, int waypoints){
        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_PATH
                + "?uid=" + userId
                + "&lat=" + usrLat
                + "&lon=" + usrLon
                + "&waypoints=" + waypoints);

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
