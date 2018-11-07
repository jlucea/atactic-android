package io.atactic.android.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.atactic.android.model.Account;
import io.atactic.android.model.AccountMap;
import io.atactic.android.model.AccountTargetingParticipation;
import io.atactic.android.model.TenantConfiguration;

public class JsonDecoder {


    public static TenantConfiguration decodeConfiguration(JSONObject tenantConfigJSON) throws JSONException {

        String accountsLiteral = tenantConfigJSON.getString("accountsLiteral");
        boolean checkInEnabled = tenantConfigJSON.getBoolean("checkInEnabled");
        String defaultLang = tenantConfigJSON.getString("defaultLanguage");
        int maxCheckInDistance = tenantConfigJSON.getInt("maxCheckInDistance");
        int numberOfWaypoints = tenantConfigJSON.getInt("numberOfRouteWaypoints");
        boolean requireProximityCheckIn = tenantConfigJSON.getBoolean("requireProximityCheckIn");
        boolean showRanking = tenantConfigJSON.getBoolean("showRanking");

        TenantConfiguration config = new TenantConfiguration();
        config.setAccountsLiteral(accountsLiteral);
        config.setCheckInEnabled(checkInEnabled);
        config.setDefaultLanguage(defaultLang);
        config.setMaxCheckInDistance(maxCheckInDistance);
        config.setRouteWaypoints(numberOfWaypoints);
        config.setProximityCheckinRequired(requireProximityCheckIn);
        config.setDisplayRanking(showRanking);

        return config;
    }

    public static List<Account> decodeAccountList(JSONObject accountAndTargetsMapJSON) throws JSONException {

        JSONArray accountsJSONArray = accountAndTargetsMapJSON.getJSONArray("accounts");

        return decodeAccountList(accountsJSONArray);
    }



    public static List<Account> decodeAccountList(JSONArray accountsJSONArray) throws JSONException {

        List<Account> accounts;
        if (accountsJSONArray != null){
            accounts = new ArrayList<>(accountsJSONArray.length());

            for (int i = 0 ; i < accountsJSONArray.length(); i++) {
                JSONObject accountJSON = accountsJSONArray.getJSONObject(i);
                Account acc = parseAccount(accountJSON);
                accounts.add(acc);
            }
            return accounts;

        } else {
            return null;
        }
    }


    public static AccountMap decodeAccountMap(JSONObject accountAndTargetsMapJSON) throws JSONException {

        AccountMap map = new AccountMap();

        // Decode accounts
        List<Account> accountList = decodeAccountList(accountAndTargetsMapJSON);
        if (accountList != null)
            map.setAccounts(accountList);

        // Decode participations and their targets
        JSONArray targetingParticipationsJSONArray = accountAndTargetsMapJSON.getJSONArray("targets");
        if (targetingParticipationsJSONArray != null) {
            List<AccountTargetingParticipation> participations = parseAccountTargetingParticipations(targetingParticipationsJSONArray);
            map.setTargetingParticipations(participations);
        }

        return map;
    }


    private static List<AccountTargetingParticipation> parseAccountTargetingParticipations(JSONArray targetingParticipationsJSONArray) throws JSONException {
        List<AccountTargetingParticipation> participations = new ArrayList<>(targetingParticipationsJSONArray.length());
        for (int p = 0; p < targetingParticipationsJSONArray.length(); p++) {
            JSONObject participationJSON = targetingParticipationsJSONArray.getJSONObject(p);
            AccountTargetingParticipation atp = parseAccountTargetingParticipation(participationJSON);
            participations.add(atp);
        }
        return participations;
    }


    private static AccountTargetingParticipation parseAccountTargetingParticipation(JSONObject participationJSON) throws JSONException {

        AccountTargetingParticipation atp = new AccountTargetingParticipation();

        // Parse header fields
        atp.setId(participationJSON.getInt("participationId"));
        atp.setCampaignName(participationJSON.getString("campaignName"));
        atp.setCurrentProgress(participationJSON.getDouble("currentProgress"));
        atp.setCompletionScore(participationJSON.getInt("completionScore"));

        // Parse targeted accounts
        JSONArray targetAccountsJSON = participationJSON.getJSONArray("targets");
        List<Account> targetAccounts = new ArrayList<>(targetAccountsJSON.length());

        for (int a = 0; a < targetAccountsJSON.length(); a++) {
            JSONObject accountJSON = targetAccountsJSON.getJSONObject(a);
            Account acc = parseAccount(accountJSON);
            targetAccounts.add(acc);
        }
        atp.setTargetAccounts(targetAccounts);

        return atp;
    }


    private static Account parseAccount(JSONObject accountJSON) throws JSONException {
        Account acc = new Account();
        acc.setId(accountJSON.getInt("id"));
        acc.setExternalId(accountJSON.getString("externalId"));
        acc.setName(accountJSON.getString("name"));
        acc.setType(accountJSON.getString("type"));
        acc.setAddress(accountJSON.getString("address"));
        acc.setPostalCode(accountJSON.getString("postalCode"));
        acc.setCity(accountJSON.getString("city"));
        acc.setProvince(accountJSON.getString("province"));
        acc.setLongitude(accountJSON.getDouble("longitude"));
        acc.setLatitude(accountJSON.getDouble("latitude"));
        return acc;
    }





}
