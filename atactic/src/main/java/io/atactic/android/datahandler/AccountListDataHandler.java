package io.atactic.android.datahandler;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;

import io.atactic.android.activity.AccountListActivity;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.manager.LocationManager;
import io.atactic.android.model.Account;
import io.atactic.android.network.request.AccountListRequest;
import io.atactic.android.utils.DistanceUtils;

import static io.atactic.android.json.JsonDecoder.decodeAccountList;

/**
 * Provides a list of accounts for an AccountListActivity to display
 */
public class AccountListDataHandler {

    private static final String LOG_TAG = "AccountListDataHandler";
    private AccountListActivity activity;


    public AccountListDataHandler(AccountListActivity activity){
        this.activity = activity;
    }

    /**
     * Gets the list of accounts by asynchronously sending an HTTP request to the server.
     * Gets the user's id from the activity's context variables.
     */
    public void getData(){

        // Retrieve user id from application variables
        int userId = ((AtacticApplication) this.activity.getApplication()).getUserId();
        Log.d(LOG_TAG, "User ID: " + userId);

        // Instantiate and execute asynchronous Http request task
        new AccountListAsyncHttpRequest(this).execute(userId);
    }

    private void sendDataToActivity(List<Account> accounts){
        // System.out.println("AccountListDataHandler - Sending data for the Activity to display (" + sublist.size() + " accounts)");

        Location currentLocation = LocationManager.getInstance().getLastKnownLocation();
        if (currentLocation != null) {
            accounts = calculateDistances(accounts, currentLocation);
        }
        activity.displayData(accounts);
    }

    private void returnErrorMessage(String message){
        activity.displayMessage(message);
    }

    private List<Account> calculateDistances(List<Account> accounts, Location userLocation){

        for(Account acc: accounts) {
            double distanceTo = DistanceUtils.disfFrom(
                    acc.getLatitude(), acc.getLongitude(),
                    userLocation.getLatitude(), userLocation.getLongitude());

            acc.setDistanceTo(distanceTo);
        }

        // Sort by distance
        // accounts.sort(Comparator.comparingDouble(Account::getDistanceTo));

        return accounts;
    }


    /**
     * Asynchronous task that performs an AccountListRequest
     */
    static class AccountListAsyncHttpRequest extends AsyncTask<Integer, Void, JSONArray> {

       private final AccountListDataHandler handler;

       AccountListAsyncHttpRequest(AccountListDataHandler dataHandler) {
            this.handler = dataHandler;
        }

        @Override
        protected JSONArray doInBackground(Integer... params) {

            // Send Http request and receive JSON response
            String response = AccountListRequest.send(params[0]);
            // Log.d("ActiveTargetsRequest", "JSON Response: " + response);

            // Return JSON array containing the data to show in the view
            try {
                return new JSONArray(response);

            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray accountsJsonArray) {
            try {
                // Decode JSON response into an Account List object
                List<Account> accountList = decodeAccountList(accountsJsonArray);
                if (accountList != null) System.out.println(accountList.size() + " accounts decoded");

                // Return data to Handler
                handler.sendDataToActivity(accountList);

            } catch (JSONException e) {
                handler.returnErrorMessage("Se ha producido un error al decodificar los datos");
            }
        }

    }

}




