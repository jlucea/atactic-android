package io.atactic.android.manager;

import android.os.AsyncTask;

import io.atactic.android.activity.ChangePasswordActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.request.ChangePasswordRequest;

public class PasswordManager {

    private ChangePasswordActivity activity;

    public PasswordManager(ChangePasswordActivity act) {
        this.activity = act;
    }

    public void changePassword(String newPwd) {

        // Execute asynchronous request
        new PasswordChangeAsyncHttpRequest().execute(newPwd);
    }


    /**
     *
     */
    public class PasswordChangeAsyncHttpRequest extends AsyncTask<String, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(String... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication)activity.getApplication()).getUserId();

            // Send UserProfileRequest
            HttpResponse response = ChangePasswordRequest.send(userId, params[0]);

            return response;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            // Analyse response and return to Activity
            if (response.getCode() == 200) {
                activity.setOk();
            }else{
                activity.setError(response.getMessage());
            }
        }
    }

}
