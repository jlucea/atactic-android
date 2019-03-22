package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

import io.atactic.android.fragment.ParticipantRankingFragment;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Participation;
import io.atactic.android.network.request.CampaignRankingRequest;

public class CampaignRankingDataHandler {

    private ParticipantRankingFragment presenter;

    public CampaignRankingDataHandler(ParticipantRankingFragment rankingPresenter){
        this.presenter = rankingPresenter;
    }


    public void getData(int userId, int campaignId){

        int[] params = {userId, campaignId};

        Log.d("CampaignRankingDataHandler", "userId=" + userId);
        Log.d("CampaignRankingDataHandler", "campaignId=" + campaignId);

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

            Log.d("CampaignRankingRequest", "userId=" + userId);
            Log.d("CampaignRankingRequest", "campaignId=" + campaignId);

            return CampaignRankingRequest.send(userId, campaignId);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            Log.d("CampaignRankingRequest", jsonArray.toString());

            try {
                List<Participation> participations = JsonDecoder.decodeParticipationList(jsonArray);

                handler.presentData(participations);

            } catch (JSONException e) {
                handler.presentError("Se ha producido un error al descodificar los datos");

            } catch (ParseException e) {
                handler.presentError("Se ha producido un error al descodificar los datos");
            }

        }
    }


}
