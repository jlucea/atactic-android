package io.atactic.android.network.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class CheckInEligibleTargetsRequest {


    private static final String LOG_TAG = "EligibleTargetsRequest";


    public static JSONArray send(int userId, float usrPosLat, float usrPosLon){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_NEARBY_ACCOUNTS
                + "?uid=" + userId
                + "&usrLat=" + usrPosLat
                + "&usrLon=" + usrPosLon);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = NetworkUtils.readStreamContent(urlConnection.getInputStream());

                return new JSONArray(responseContent);
            }
        } catch(JSONException je) {
            je.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }

}
