package io.atactic.android.datahandler;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Comparator;
import java.util.List;

import io.atactic.android.activity.CampaignDetailActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.manager.LocationManager;
import io.atactic.android.model.Account;
import io.atactic.android.network.request.CampaignTargetsRequest;
import io.atactic.android.utils.DistanceUtils;

public class ParticipationTargetsDataHandler {

    private final static String LOG_TAG = "ParticipationTargetsDataHandler";

    // TODO Ideally, this Data Handler would contain an Account List Fragment instead
    private CampaignDetailActivity activity;

    public ParticipationTargetsDataHandler(CampaignDetailActivity activity){
        this.activity = activity;
    }

    public void getData(int participationId){

        LocationManager.getInstance().updateLocation(activity);
        final int userId = ((AtacticApplication)activity.getApplication()).getUserId();

        Log.v(LOG_TAG,"Requesting targets for participation " + participationId);
        new ParticipationTargetsRequestTask(this).execute(new QuestTargetsRequestParams(userId, participationId));
    }

    private void returnData(List<Account> data){

        Log.d(LOG_TAG,"Returning " + data.size() + " accounts to activity");

        Location location = LocationManager.getInstance().getLastKnownLocation();
        if (location != null)
            data = calculateDistances(data, location);

        activity.displayTargets(data);
    }

    private List<Account> calculateDistances(List<Account> accounts, Location userLocation){

        for(Account acc: accounts) {
            double distanceTo = DistanceUtils.disfFrom(
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

    static class ParticipationTargetsRequestTask extends AsyncTask<QuestTargetsRequestParams, Void, String> {

        private ParticipationTargetsDataHandler handler;

        ParticipationTargetsRequestTask(ParticipationTargetsDataHandler dataHandler){
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
            // Log.d("ParticipationTargetsRequestTask", "JSON array received: " + jsonArrayString);

            if (jsonArrayString != null) {
                try {
                    JSONArray targetsJSON = new JSONArray(jsonArrayString);

                    List<Account> targets = JsonDecoder.decodeAccountList(targetsJSON);
                    handler.returnData(targets);

                } catch (JSONException err) {
                    Log.e("ParticipationTargetsRequestTask", "Error while decoding targets JSON array", err);
                    // TODO Return message to Activity
                }
            }
        }
    }


}
