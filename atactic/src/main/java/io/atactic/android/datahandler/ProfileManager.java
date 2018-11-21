package io.atactic.android.datahandler;

import android.os.AsyncTask;

import org.json.JSONObject;

import io.atactic.android.activity.ProfileActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.network.request.UserProfileRequest;

public class ProfileManager {

    private ProfileActivity activity;


    public ProfileManager(ProfileActivity a){
        this.activity = a;
    }

    public void getData(){

        // Send asynchronous request
        new UserProfileAsyncHttpRequest().execute();
    }


    public class UserProfileAsyncHttpRequest extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication)activity.getApplication()).getUserId();

            // Send UserProfileRequest
            JSONObject response = UserProfileRequest.send(userId);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            activity.setData(jsonObject);
        }
    }


}
