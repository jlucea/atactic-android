package io.atactic.android.datahandler;

import android.os.AsyncTask;

import io.atactic.android.activity.ChangePasswordActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.request.ChangePasswordRequest;
import io.atactic.android.utils.CredentialsCache;

public class PasswordManager {

    private ChangePasswordActivity activity;

    public PasswordManager(ChangePasswordActivity act) {
        this.activity = act;
    }

    public void changePassword(int userId, String newPwd) {

        // Execute asynchronous request
        new PasswordChangeAsyncHttpRequest().execute(new PasswordChangeParams(userId, newPwd));
    }


    private class PasswordChangeParams {
        int userId;
        String newPassword;

        PasswordChangeParams(int uid, String npwd){
            userId = uid;
            newPassword = npwd;
        }
    }


    public class PasswordChangeAsyncHttpRequest extends AsyncTask<PasswordChangeParams, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(PasswordChangeParams... params) {

            // Send UserProfileRequest
            HttpResponse response = ChangePasswordRequest.send(params[0].userId, params[0].newPassword);

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
