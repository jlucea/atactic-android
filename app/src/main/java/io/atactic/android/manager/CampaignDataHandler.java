package io.atactic.android.manager;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Comparator;
import java.util.List;

import io.atactic.android.activity.QuestDetailActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Account;
import io.atactic.android.network.request.CampaignTargetsRequest;
import io.atactic.android.utils.DistanceCalculator;

public class CampaignDataHandler {

    private QuestDetailActivity activity;

    public CampaignDataHandler(QuestDetailActivity activity){
        this.activity = activity;
    }

    public void getCampaignTargets(int participationId){

        LocationManager.getInstance().updateLocation(activity);

        final int userId = ((AtacticApplication)activity.getApplication()).getUserId();

        QuestTargetsRequestParams params = new QuestTargetsRequestParams(userId, participationId);

        new QuestTargetsHttpRequest(this).execute(params);
    }

    private void returnData(List<Account> data){

        Location location = LocationManager.getInstance().getLastKnownLocation();
        if (location != null)
            data = calculateDistances(data, location);

        activity.displayCampaignTargets(data);
    }

    private List<Account> calculateDistances(List<Account> accounts, Location userLocation){

        for(Account acc: accounts) {
            double distanceTo = DistanceCalculator.disfFrom(
                    acc.getLatitude(), acc.getLongitude(),
                    userLocation.getLatitude(), userLocation.getLongitude());

            acc.setDistanceTo(distanceTo);
        }

        // Sort by distance
        accounts.sort(Comparator.comparingDouble(Account::getDistanceTo));

        return accounts;
    }


    private class QuestTargetsRequestParams {
        int userId;
        int participationId;

        QuestTargetsRequestParams(int uid, int pid){
            this.userId = uid;
            this.participationId = pid;
        }
    }

    static class QuestTargetsHttpRequest extends AsyncTask<QuestTargetsRequestParams, Void, String> {

        private CampaignDataHandler handler;

        QuestTargetsHttpRequest(CampaignDataHandler dataHandler){
            this.handler = dataHandler;
        }

        @Override
        protected String doInBackground(QuestTargetsRequestParams... params) {

            // Retrieve user identification from global variables
            int userId = params[0].userId;
            int participationId = params[0].participationId;

            return CampaignTargetsRequest.send(userId, participationId);
        }

        @Override
        protected void onPostExecute(String jsonArrayString) {
            Log.d("QuestTargetsHttpRequest", "JSON array received: " + jsonArrayString);

            if (jsonArrayString != null) {
                try {
                    JSONArray targetsJSON = new JSONArray(jsonArrayString);

                    // TODO Decode data here
                    List<Account> targets = JsonDecoder.decodeAccountList(targetsJSON);


                    handler.returnData(targets);

                } catch (JSONException err) {
                    Log.e("QuestTargetsHttpRequest", "Error while parsing targets array", err);
                    // TODO Return message to Activity
                }
            }
        }
    }


}
