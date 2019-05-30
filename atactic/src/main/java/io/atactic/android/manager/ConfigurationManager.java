package io.atactic.android.manager;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.TenantConfiguration;
import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.request.TenantConfigurationRequest;

public class ConfigurationManager {

    private static ConfigurationManager uniqueInstance = new ConfigurationManager();

    private final static String LOG_TAG = "ConfigurationManager";

    private TenantConfiguration lastKnownConfig;

    private ConfigurationManager(){
        // Private constructor prevents instancing from outside of the class
    }

    public static ConfigurationManager getInstance(){
        return uniqueInstance;
    }

    public void requestUpdatedConfiguration(int userId){
        Log.v(LOG_TAG,"Requesting updated tenant configuration values");

        new ConfigurationRequestAsyncTask(this).execute(userId);
    }

    public TenantConfiguration getConfiguration(){
        return lastKnownConfig;
    }

    private void updateLastKnownConfiguration(TenantConfiguration newconfig){
        Log.v(LOG_TAG,"Tenant configuration values have been updated");
        this.lastKnownConfig = newconfig;
    }


    private void handleResponse(HttpResponse response){

        if (response != null) {
            if (response.getCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject configJSON = new JSONObject(response.getContent());
                    TenantConfiguration configuration = JsonDecoder.decodeConfiguration(configJSON);
                    updateLastKnownConfiguration(configuration);

                } catch (JSONException | NullPointerException err) {
                    Log.e(LOG_TAG, "Error decoding tenant configuration", err);
                    if (lastKnownConfig == null)
                        Log.w(LOG_TAG, "Operating with unknown tenant configuration values");
                }
            }
        }else{
            Log.e(LOG_TAG, "Null response from server");
            if (lastKnownConfig == null)
                Log.w(LOG_TAG, "Operating with unknown tenant configuration values");
        }

    }

    private static class ConfigurationRequestAsyncTask extends AsyncTask<Integer, Void, HttpResponse> {

        private ConfigurationManager manager;

        ConfigurationRequestAsyncTask(ConfigurationManager manager){
            this.manager = manager;
        }

        @Override
        protected HttpResponse doInBackground(Integer... integers) {
            return TenantConfigurationRequest.send(integers[0]);
        }


        @Override
        protected void onPostExecute(HttpResponse response) {
            manager.handleResponse(response);
        }
    }

}
