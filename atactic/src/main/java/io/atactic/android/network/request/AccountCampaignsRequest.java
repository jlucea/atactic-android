package io.atactic.android.network.request;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class AccountCampaignsRequest {

    private static final String LOG_TAG = "AccountCampaignsRequest";

    private static final String PARAM_USERID = "uid";
    private static final String PARAM_ACCOUNTID = "accid";

    public static HttpResponse send (int userId, int accountId){

        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_ACCOUNT_CAMPAIGNS
                + "?" + PARAM_USERID + "=" + userId
                + "&" + PARAM_ACCOUNTID + "=" + accountId);

        Log.d(LOG_TAG,url.toString());

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
