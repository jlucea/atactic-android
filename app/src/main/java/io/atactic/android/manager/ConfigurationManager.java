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
        this.lastKnownConfig = newconfig;
    }

    private static class ConfigurationRequestAsyncTask extends AsyncTask<Integer, Void, JSONObject> {

        private ConfigurationManager manager;

        ConfigurationRequestAsyncTask(ConfigurationManager manager){
            this.manager = manager;
        }

        @Override
        protected JSONObject doInBackground(Integer... integers) {

            HttpResponse response = TenantConfigurationRequest.send(integers[0]);
            if (response.getCode() == HttpURLConnection.HTTP_OK) {
                try {
                    return new JSONObject(response.getMessage());

                }catch (JSONException jsonerr){
                    jsonerr.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            try {
                TenantConfiguration configuration = JsonDecoder.decodeConfiguration(jsonObject);
                if (configuration != null) {
                    Log.v(LOG_TAG,"Tenant configuration values have been updated");
                    manager.updateLastKnownConfiguration(configuration);
                }
            }catch (JSONException jsonex){
                Log.e(LOG_TAG,"Error when requesting tenant configuration", jsonex);
                jsonex.printStackTrace();
            }
        }
    }

}
