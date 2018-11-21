package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.List;

import io.atactic.android.activity.HistoryActivity;
import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Visit;
import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.request.ActivityHistoryRequest;

public class ActivityHistoryDataHandler {

    private String LOG_TAG = "ActivityHistoryDataHandler";

    private HistoryActivity activity;

    public ActivityHistoryDataHandler(HistoryActivity activity){
        this.activity = activity;
    }


    public void getData(int userId){
        new ActivityHistoryAsyncHttpRequest(this).execute(userId);
    }


    private void returnData(List<Visit> activities){
        activity.displayData(activities);
    }

    private void returnMessage(String message){
        activity.displayMessage(message);
    }


    static class ActivityHistoryAsyncHttpRequest extends AsyncTask<Integer, Void, HttpResponse> {

        private ActivityHistoryDataHandler handler;

        ActivityHistoryAsyncHttpRequest(ActivityHistoryDataHandler dataHandler){
            handler = dataHandler;
        }

        @Override
        protected HttpResponse doInBackground(Integer... params) {
            return ActivityHistoryRequest.send(params[0]);
        }

        @Override
        protected void onPostExecute(HttpResponse response) {

            if (response != null) {
                if (response.getCode() == HttpURLConnection.HTTP_OK) {

                    try {
                        // Decode JSON Array into a list of Visit objects
                        JSONArray jsonArray = new JSONArray(response.getMessage());
                        List<Visit> visits = JsonDecoder.decodeActivityList(jsonArray);

                        handler.returnData(visits);

                    } catch (JSONException | ParseException err) {
                        Log.e(handler.LOG_TAG, "Error decoding activity list JSON Array", err);
                        handler.returnMessage("Se ha producido un error al decodificar los datos");
                    }
                } else {
                    Log.e(handler.LOG_TAG, "Server error - " + response.getCode());
                    handler.returnMessage("Se ha producido un error en el servidor (" + response.getCode() + ")");
                }
            }else{
                Log.e(handler.LOG_TAG, "NULL Http response");
                handler.returnMessage("No se ha podido conectar con el servidor");
            }
        }

    }

}
