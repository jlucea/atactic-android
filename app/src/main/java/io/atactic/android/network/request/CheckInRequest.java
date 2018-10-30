package io.atactic.android.network.request;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class CheckInRequest {

    private static final String LOG_TAG = "CheckInRequest";


    /**
     * Send a check-in Http request to the check-in resource in the ATACTIC API
     *
     * @param userId Identifier of the user who is performing the check-in
     * @param accountId Visited account's identifier
     * @param comments Comments on the visit
     * @param usrPosLatitude User's location latitude
     * @param usrPosLongitude User's location longitude
     */
    public static HttpResponse send(int userId, int accountId, String comments,
                                    float usrPosLatitude, float usrPosLongitude){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_CHECKIN);
        Log.d(LOG_TAG, url.toString());

        try {
            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String params = "uid="+userId+"&account="+accountId+"&comments="+comments+"&lat="
                    +usrPosLatitude+"&lon="+usrPosLongitude;

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
