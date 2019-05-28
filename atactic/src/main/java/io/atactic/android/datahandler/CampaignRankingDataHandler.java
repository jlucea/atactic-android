package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.atactic.android.fragment.ParticipantRankingFragment;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Participation;
import io.atactic.android.network.request.CampaignRankingRequest;
import io.atactic.android.utils.CredentialsCache;

public class CampaignRankingDataHandler {

    private static final String LOG_TAG = "CRankingDataHandler";

    private ParticipantRankingFragment presenter;


    public CampaignRankingDataHandler(ParticipantRankingFragment rankingPresenter){
        this.presenter = rankingPresenter;
    }


    public void getData(int campaignId) {

        CredentialsCache.UserCredentials credentials = CredentialsCache.recoverCredentials();

        if (credentials != null) {
            int[] params = {credentials.getUserId(), campaignId};

            new CampaignRankingAsyncHttpRequest(this).execute(params);

        } else {
            presentError("Error de seguridad. Por favor, reinicie la app");
        }
    }


    public void getData(int userId, int campaignId){

        int[] params = {userId, campaignId};

        Log.d(LOG_TAG, "userId=" + userId);
        Log.d(LOG_TAG, "campaignId=" + campaignId);

        new CampaignRankingAsyncHttpRequest(this).execute(params);
    }

    private void presentData(List<Participation> participations){
        presenter.displayProgressRanking(participations);
    }

    private void presentError(String message){
        presenter.displayMessage(message);
    }


    static class CampaignRankingAsyncHttpRequest extends AsyncTask<int[], Void, JSONArray> {

        private final CampaignRankingDataHandler handler;

        CampaignRankingAsyncHttpRequest(CampaignRankingDataHandler dataHandler){
            handler = dataHandler;
        }

        @Override
        protected JSONArray doInBackground(int[]... params) {

            int userId = params[0][0];
            int campaignId = params[0][1];

            Log.d(LOG_TAG, "userId=" + userId);
            Log.d(LOG_TAG, "campaignId=" + campaignId);

            return CampaignRankingRequest.send(userId, campaignId);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            Log.d(LOG_TAG, jsonArray.toString());

            try {
                List<Participation> participations = JsonDecoder.decodeParticipationList(jsonArray);

                Collections.sort(participations, new ProgressComparator());

                handler.presentData(participations);

            } catch (JSONException e) {
                handler.presentError("Se ha producido un error al descodificar los datos");

            } catch (ParseException e) {
                handler.presentError("Se ha producido un error al descodificar los datos");
            }
        }


        private class ProgressComparator implements Comparator<Participation>{

            @Override
            public int compare(Participation o1, Participation o2) {
                if (o1.getCurrentProgress() < o2.getCurrentProgress()) {
                    return 1;
                } else if (o1.getCurrentProgress() > o2.getCurrentProgress()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }


    }


}
