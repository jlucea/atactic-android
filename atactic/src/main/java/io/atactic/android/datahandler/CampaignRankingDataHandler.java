package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.atactic.android.fragment.ParticipantRankingFragment;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Participation;
import io.atactic.android.network.HttpResponse;
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
            Log.wtf(LOG_TAG, "Unable to recover user credentials");
            presenter.displayMessage("Error de seguridad. Por favor, reinicie la app");
        }
    }

    public void getData(int userId, int campaignId){

        int[] params = {userId, campaignId};

        Log.d(LOG_TAG, "userId=" + userId);
        Log.d(LOG_TAG, "campaignId=" + campaignId);

        new CampaignRankingAsyncHttpRequest(this).execute(params);
    }


    private void handleResponse(HttpResponse response) {

        if (response != null) {
            if (response.getCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONArray participationsJSONArray = new JSONArray(response.getContent());
                    List<Participation> participations = JsonDecoder.decodeParticipationList(participationsJSONArray);

                    if (participations != null) {
                        Collections.sort(participations, new ProgressComparator());
                        presenter.displayProgressRanking(participations);

                    } else {
                        Log.wtf(LOG_TAG, "Null ranking");
                        presenter.displayMessage("No se encontraron participantes");
                    }
                } catch (JSONException | ParseException |NullPointerException err) {
                    Log.e(LOG_TAG, "JSON error decoding response", err);
                    presenter.displayMessage("Se ha producido un error al descodificar los datos");
                }
            }else{
                Log.e(LOG_TAG, "Server error - " + response.getCode());
                presenter.displayMessage("Error al consultar el ranking (error de servidor)");
            }
        } else {
            Log.e(LOG_TAG, "No response from server");
            presenter.displayMessage("Error al consultar el ranking (no hay conexión)");
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


    static class CampaignRankingAsyncHttpRequest extends AsyncTask<int[], Void, HttpResponse> {

        private final CampaignRankingDataHandler handler;

        CampaignRankingAsyncHttpRequest(CampaignRankingDataHandler dataHandler){
            handler = dataHandler;
        }

        @Override
        protected HttpResponse doInBackground(int[]... params) {

            int userId = params[0][0];
            int campaignId = params[0][1];

            return CampaignRankingRequest.send(userId, campaignId);
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            handler.handleResponse(response);
        }

    }


}
