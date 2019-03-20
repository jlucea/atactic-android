package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.util.List;

import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Participation;
import io.atactic.android.network.request.QuestListRequest;
import io.atactic.android.presenter.ParticipationListPresenter;

public class ParticipationListDataHandler {

    private ParticipationListPresenter presenter;

    private static final String LOG_TAG = "ParticipationListDataHandler";

    public ParticipationListDataHandler(ParticipationListPresenter activity){
        this.presenter = activity;
    }

    public void getData(int userId){
        new CampaignListAsyncHttpRequest(this).execute(userId);
    }


    private void handleResponse(String response){
        if (response != null) {
            Log.v(LOG_TAG,"Response received");

            try {
                JSONArray participationsJSON = new JSONArray(response);

                // Log.d(LOG_TAG, participationsJSON.toString());

                // Decode JSON Array into a List<Participation> object
                Log.v(LOG_TAG, "Decoding participation list from JSON response ...");
                List<Participation> participationList = JsonDecoder.decodeParticipationList(participationsJSON);
                Log.d(LOG_TAG, participationList.size() + " participations decoded");

                for (Participation p : participationList){
                    Log.d(LOG_TAG, p.getId() + " - " + p.getCampaign().getName());
                }


                // Send participation list to presenter
                presenter.displayCampaignList(participationList);

            }catch (Exception ex){
                Log.e(LOG_TAG, "JSON error while decoding Campaign List JSON", ex);
                presenter.displayMessage("Se ha producido un error al leer la lista de campañas");
            }
        } else {
            Log.w(LOG_TAG,"No response from server");
            presenter.displayMessage("No hay conexión con el servidor");
        }
    }


    /**
     * This class' execute method sends an asynchronous http request to the server
     * and sends the returning JSON Array to the ParticipationListAdapter.
     *
     */
    static class CampaignListAsyncHttpRequest extends AsyncTask<Integer, Void, String> {

        private ParticipationListDataHandler handler;

        CampaignListAsyncHttpRequest(ParticipationListDataHandler dataHandler){
            this.handler = dataHandler;
        }

        @Override
        protected String doInBackground(Integer... params) {

            // Send Http request and return JSON response as a String
            // TODO Return an HttpResponse object instead
            return QuestListRequest.send(params[0]);
        }

        @Override
        protected void onPostExecute(String questData) {
            handler.handleResponse(questData);
        }

    }

}

