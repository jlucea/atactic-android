package io.atactic.android.datahandler;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;

import io.atactic.android.R;
import io.atactic.android.activity.CampaignListActivity;
import io.atactic.android.activity.LoginActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.manager.ConfigurationManager;
import io.atactic.android.network.LoginResponse;
import io.atactic.android.network.request.LoginRequest;

public class AuthenticationHandler {

    private static final String LOG_TAG = "AuthenticationHandler";

    private final LoginActivity activity;

    public AuthenticationHandler(LoginActivity loginActivity){
        this.activity = loginActivity;
    }

    public void attemptLogin(String username, String password){
        new LoginAsyncTask(this, username,password).execute();
    }


    private void storeUserCredentialsInAppCache(int userId, String email, String password){
        ((AtacticApplication)activity.getApplication()).setUserId(userId);
        ((AtacticApplication)activity.getApplication()).setUserName(email);
        ((AtacticApplication)activity.getApplication()).setPassword(password);
    }

    private void enterApp(){
        Log.v(LOG_TAG, "Entering app ...");
        Intent i = new Intent(activity, CampaignListActivity.class);
        activity.startActivity(i);
    }

    private void handleResponse(String username, String password, LoginResponse response){
        if (response != null) {
            if (response.isOk()) {
                int userId = Integer.parseInt(response.getContent());

                Log.v(LOG_TAG, "Authentication successful");
                Log.d(LOG_TAG, "User ID = " + userId);

                // Store credentials
                Log.v(LOG_TAG, "Storing user credentials in app cache");
                storeUserCredentialsInAppCache(userId, username, password);

                // Start configuration manager and request configuration
                Log.v(LOG_TAG, "Requesting configuration");
                ConfigurationManager.getInstance().requestUpdatedConfiguration(userId);

                // Enter App
                enterApp();

                activity.resetViews();

            } else {
                Log.w(LOG_TAG, "Authentication failed");
                Log.d(LOG_TAG, "Response Code = " + response.getResponseCode());

                activity.resetViews();
                if (response.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    activity.displayErrorMessage(activity.getString(R.string.err_login_credentials));
                } else {
                    activity.displayErrorMessage(activity.getString(R.string.err_login_unknown)
                                    + " (Error " + response.getResponseCode() + ")");
                }
            }
        }else{
            Log.e(LOG_TAG, "Response from authentication service is NULL");
            activity.displayErrorMessage(activity.getString(R.string.err_login_connection));
        }
    }

    /**
     * An asynchronous login task used to authenticate the user.
     */
    private static class LoginAsyncTask extends AsyncTask<Void, Void, LoginResponse> {

        private AuthenticationHandler manager;

        private final String mEmail;
        private final String mPassword;

        // private final String userToken;

        LoginAsyncTask(AuthenticationHandler manager, String email, String password) {
            mEmail = email;
            mPassword = password;
            this.manager = manager;
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            /*
             * Send Http request attempting login based on credentials
             */
            Log.v("LoginAsyncTask", "Executing LoginRequest");
            return LoginRequest.send(mEmail,mPassword);
        }

        @Override
        protected void onPostExecute(final LoginResponse response) {
            manager.handleResponse(mEmail, mPassword, response);
        }
    }


}
