package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Account;
import io.atactic.android.model.AccountMap;
import io.atactic.android.model.AccountTargetingParticipation;
import io.atactic.android.model.ParticipationSummary;
import io.atactic.android.model.TargetAccount;
import io.atactic.android.network.request.AccountMapRequest;
import io.atactic.android.presenter.MapDataPresenter;

public class MapDataHandler {

    private static final String LOG_TAG = "MapDataHandler";

    private MapDataPresenter presenter;


    public MapDataHandler(MapDataPresenter dataPresenter){
        this.presenter = dataPresenter;
    }


    public void getData(int userId){

        // Instantiate and execute asynchronous Http request task
        new AccountMapAsyncHttpRequest(this).execute(userId);
    }

    private void sendDataToMapPresenter(AccountMap accountMap){

        Log.d(LOG_TAG, "Map contains " + accountMap.getAccounts().size() + " accounts" +
                " and " + accountMap.getTargetingParticipations().size() + " targeting participations");

        AccountsAndTargets data = transform(accountMap);

        Log.d(LOG_TAG, "After transformation: "
                + data.accounts.size() + " accounts | " +
                + data.targets.size() + " targets");

        Log.v(LOG_TAG, "Returning data to MapDataPresenter");
        this.presenter.displayMarkers(data.accounts, data.targets);
    }


    /*
     * Internal container class
     */
    private class AccountsAndTargets {

        List<Account> accounts;
        List<TargetAccount> targets;

    }

    /**
     * Will transform the Account Map returned from the server, which contains all Accounts and
     * Participations targeting some accounts into a private AccountsAndTargets structure,
     * that will hold a list of TargetAccounts on one hand and a list with the rest of the accounts
     * (those that are not targeted by any campaign) on the other.
     *
     * @param accountMap Map with Accounts
     * @return Private structure that facilitates displaying data
     */
    private AccountsAndTargets transform(AccountMap accountMap){

        List<TargetAccount> addedTargetsTmpList = new ArrayList<>();
        List<TargetAccount> targets = new ArrayList<>();

        // Iterate through participations targeting accounts
        for (AccountTargetingParticipation participation : accountMap.getTargetingParticipations()){

            List<Account> targetAccounts = participation.getTargetAccounts();
            for (Account acc : targetAccounts){

                if (contains(acc, addedTargetsTmpList)){
                    // If the accounts has already been added as a target, add the participation
                    TargetAccount target = findTargetAccount(acc.getId(), targets);
                    target.getParticipations().add(participation);

                } else {
                    // If it's a new target, add the account to the target list as such
                    TargetAccount newTarget = new TargetAccount(acc);

                    // Create a new ParticipationSummary list
                    List<ParticipationSummary> tgtPts = new ArrayList<>();

                    // Add a new ParticipationSummary object
                    ParticipationSummary p = new ParticipationSummary();
                    p.setId(participation.getId());
                    p.setCampaignName(participation.getCampaignName());
                    p.setCurrentProgress(participation.getCurrentProgress());
                    p.setCompletionScore(participation.getCompletionScore());

                    tgtPts.add(participation);
                    newTarget.setParticipations(tgtPts);
                    targets.add(newTarget);
                    addedTargetsTmpList.add(newTarget);
                }
            }
        }

        List<Account> accounts = new ArrayList<>();

        // Add to the accounts list all the Accounts that are not in the targets list
        for (Account acc : accountMap.getAccounts()) {
            if (!contains(acc, targets)){
                accounts.add(acc);
            }
        }

        AccountsAndTargets result = new AccountsAndTargets();
        result.accounts = accounts;
        result.targets = targets;

        return result;
    }


    private boolean contains (Account acc, List<TargetAccount> accountList){
        return findTargetAccount(acc.getId(), accountList) != null;
    }


    private TargetAccount findTargetAccount(int accountId, List<TargetAccount> accountList){
        for (TargetAccount acc: accountList){
            if (acc.getId() == accountId) return acc;
        }
        return null;
    }

    /**
     * Asynchronous task that performs an AccountListRequest
     */
    private static class AccountMapAsyncHttpRequest extends AsyncTask<Integer, Void, JSONObject> {

        private final MapDataHandler handler;

        AccountMapAsyncHttpRequest(MapDataHandler dataHandler) {
            this.handler = dataHandler;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {

            // Send Http request and receive JSON response
            String response = AccountMapRequest.send(params[0]);
            // Log.d("AccountMapRequestTask", "JSON Response: " + response);

            // Return JSON array containing the data to show in the view
            try {

                if (response != null) {
                    return new JSONObject(response);
                }

            } catch (Exception err) {
                Log.e(LOG_TAG, "Invalid server response error", err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject accountAndTargetsMapJSON) {

            if (accountAndTargetsMapJSON != null) {

                try {
                    // Decode JSON response into an Account List object
                    AccountMap accountMap = JsonDecoder.decodeAccountMap(accountAndTargetsMapJSON);
                    if (accountMap != null) {
                        Log.v(LOG_TAG, "Account Map successfully decoded");
                        Log.d(LOG_TAG, accountMap.getAccounts().size() + " accounts decoded");

                        // Return data to Handler
                        handler.sendDataToMapPresenter(accountMap);

                    } else {
                        Log.e(LOG_TAG,"Null Map data");
                        handler.presenter.displayError("Se ha producido un error al cargar los datos del mapa");
                    }

                } catch (JSONException err) {
                    Log.e(LOG_TAG,"JSON error when decoding account map data", err);
                    handler.presenter.displayError("Se ha producido un error al cargar los datos del mapa");
                }
            } else {
                handler.presenter.displayError("No se han podido cargar los datos del mapa");
            }

        }

    }

}
