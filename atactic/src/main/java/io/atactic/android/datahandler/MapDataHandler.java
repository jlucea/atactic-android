package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import io.atactic.android.json.JsonDecoder;
import io.atactic.android.model.Account;
import io.atactic.android.model.AccountMap;
import io.atactic.android.model.AccountTargetingParticipation;
import io.atactic.android.model.ParticipationSummary;
import io.atactic.android.model.TargetAccount;
import io.atactic.android.network.HttpResponse;
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


    private void handleResponse(HttpResponse response) {

        if (response != null) {
            if (response.getCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject accountAndTargetsMapJSON = new JSONObject(response.getContent());
                    AccountMap accountMap = JsonDecoder.decodeAccountMap(accountAndTargetsMapJSON);
                    Log.v(LOG_TAG, "Account Map successfully decoded");
                    Log.d(LOG_TAG, "Map contains " + accountMap.getAccounts().size() + " accounts" +
                            " and " + accountMap.getTargetingParticipations().size() + " targeting participations");

                    // Transform data into a presenter-friendly structure
                    AccountsAndTargets data = transform(accountMap);

                    Log.d(LOG_TAG, "After transformation: "
                            + data.accounts.size() + " accounts | " +
                            + data.targets.size() + " targets");

                    // Return data to presenter
                    Log.v(LOG_TAG, "Returning data to MapDataPresenter");
                    this.presenter.displayMarkers(data.accounts, data.targets);

                } catch (Exception err) {
                    Log.e(LOG_TAG, "Exception while decoding map data", err);
                    presenter.displayError("Error al leer los datos del mapa");
                }
            } else {
                Log.e(LOG_TAG, "Invalid server response - Error" + response.getCode());
                presenter.displayError("Error al cargar los datos del mapa (error " + response.getCode() + ")");
            }
        } else {
            Log.e(LOG_TAG,"No response from server");
            presenter.displayError("Error al cargar los datos del mapa (no hay conexión)");
        }
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
     * Asynchronous task that performs an Http request to retrieve accounts to display on a map
     */
    private static class AccountMapAsyncHttpRequest extends AsyncTask<Integer, Void, HttpResponse> {

        private final MapDataHandler handler;

        AccountMapAsyncHttpRequest(MapDataHandler dataHandler) {
            this.handler = dataHandler;
        }

        @Override
        protected HttpResponse doInBackground(Integer... params) {

            // Send Http request and receive Http response
            return AccountMapRequest.send(params[0]);
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            handler.handleResponse(response);
        }

    }

}
