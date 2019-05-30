package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.List;

import io.atactic.android.activity.AccountDetailActivity;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Participation;
import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.request.AccountCampaignsRequest;

public class AccountParticipationsDataHandler {

    private static final String LOG_TAG = "AccountParticipsDH";

    private AccountDetailActivity activity;

    public AccountParticipationsDataHandler(AccountDetailActivity accountDetailActivity){
        this.activity = accountDetailActivity;
    }

    public void getData(int userId, int accountId){
        AccountParticipationsRequestParams params = new AccountParticipationsRequestParams(userId, accountId);
        new AccountParticipationsAsyncRequestTask(this).execute(params);
    }

    private void handleResponse(HttpResponse response){

        if (response != null){
            Log.v(LOG_TAG, "Response received");
            if (response.getCode() == HttpURLConnection.HTTP_OK){

                try {
                    JSONArray participationsJSONArray = new JSONArray(response.getContent());
                    List<Participation> participationList = JsonDecoder.decodeParticipationList(participationsJSONArray);

                    if (participationList != null) {
                        Log.d(LOG_TAG, participationList.size() + " participations related to the account were found");
                        activity.displayParticipations(participationList);
                    } else{
                        Log.d(LOG_TAG, "Null participation list");
                        activity.displayMessage("No se encontraron campañas relacionadas");
                    }

                } catch (JSONException | ParseException | NullPointerException jsonErr){
                    Log.e(LOG_TAG, "JSON error decoding response", jsonErr);
                    activity.displayMessage("Error al consultar las campañas");
                }
            }else{
                Log.e(LOG_TAG, "Server error - " + response.getCode());
                activity.displayMessage("Error al consultar las campañas (error de servidor)");
            }
        } else {
            Log.e(LOG_TAG,"No response from server");
            activity.displayMessage("Error al consultar las campañas (no hay conexión)");
        }
    }


    private class AccountParticipationsRequestParams {
        int userId;
        int accountId;

        AccountParticipationsRequestParams(int uid, int accId){
            userId = uid;
            accountId = accId;
        }
    }


    static class AccountParticipationsAsyncRequestTask extends AsyncTask<AccountParticipationsRequestParams, Void, HttpResponse> {

        private AccountParticipationsDataHandler handler;

        AccountParticipationsAsyncRequestTask(AccountParticipationsDataHandler dataHandler){
            this.handler = dataHandler;
        }

        @Override
        protected HttpResponse doInBackground(AccountParticipationsRequestParams... params) {
            return AccountCampaignsRequest.send(params[0].userId, params[0].accountId);
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            handler.handleResponse(response);
        }
    }

}
