package io.atactic.android.json;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.atactic.android.model.Account;
import io.atactic.android.model.AccountMap;
import io.atactic.android.model.AccountTargetingParticipation;
import io.atactic.android.model.Campaign;
import io.atactic.android.model.Participation;
import io.atactic.android.model.TenantConfiguration;
import io.atactic.android.model.User;
import io.atactic.android.model.Visit;
import io.atactic.android.utils.DateUtils;

public class JsonDecoder {


    public static List<Visit> decodeActivityList(JSONArray activitiesJSONArray) throws JSONException, ParseException {

        List<Visit> activities = new ArrayList<>(activitiesJSONArray.length());
        for (int i = 0; i < activitiesJSONArray.length(); i++) {
            Visit v = decodeVisit(activitiesJSONArray.getJSONObject(i));
            activities.add(v);
        }
        return activities;
    }


    public static Visit decodeVisit(JSONObject visitJSON) throws JSONException, ParseException {

        Visit v = new Visit();
        Account account = parseAccount(visitJSON.getJSONObject("account"));
        v.setAccount(account);

        v.setComments(visitJSON.getString("comments"));

        String dateStr = visitJSON.getString("timeReported");
        Date date = DateUtils.parseDate(dateStr);
        v.setDate(date);

        return v;
    }



    public static Campaign decodeCampaign(JSONObject campaignJSON) throws JSONException, ParseException {

        int campaignId = campaignJSON.getInt("id");
        String campaignName = campaignJSON.getString("name");

        String type = campaignJSON.getString("campaignClass");
        String status = campaignJSON.getString("status");
        int priority = campaignJSON.getInt("priority");
        int completionScore = campaignJSON.getInt("completionScore");

        String briefing = campaignJSON.getString("summary");
        String description = campaignJSON.getString("description");

        String startDateStr = campaignJSON.getString("startDate");
        String endDateStr = campaignJSON.getString("endDate");

        // Parse Dates
        Date startDate = DateUtils.parseDate(startDateStr);
        Date endDate = DateUtils.parseDate(endDateStr);

        User owner = decodeUser(campaignJSON.getJSONObject("owner"));

        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        campaign.setName(campaignName);
        campaign.setType(type);
        campaign.setStatus(status);
        campaign.setPriority(priority);
        campaign.setCompletionScore(completionScore);
        campaign.setBriefing(briefing);
        campaign.setDescription(description);
        campaign.setStartDate(startDate);
        campaign.setEndDate(endDate);
        campaign.setOwner(owner);

        return campaign;
    }


    public static User decodeUser(JSONObject userJSON) throws JSONException {

        // Decode required data
        int id = userJSON.getInt("userId");
        String email = userJSON.getString("email");
        String firstName = userJSON.getString("firstName");
        String lastName = userJSON.getString("lastName");
        String position = userJSON.getString("position");
        int score = userJSON.getInt("score");

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPosition(position);
        user.setCurrentScore(score);

        // Decode and set optional data
        try {
            String extId = userJSON.getString("externalId");
            user.setExternalId(extId);
        } catch (JSONException ex){
            // No external ID - That's OK
        }
        return user;
    }


    public static Participation decodeParticipation(JSONObject participationJSON) throws JSONException, ParseException {

        int participationId = participationJSON.getInt("participationId");
        Campaign campaign = decodeCampaign(participationJSON.getJSONObject("campaign"));
        User participant = decodeUser(participationJSON.getJSONObject("participant"));
        double currentProgress = participationJSON.getDouble("currentProgress");

        double currentValue = participationJSON.getDouble("currentValue");
        double targetValue = participationJSON.getDouble("targetValue");


        Participation participation = new Participation();
        participation.setId(participationId);
        participation.setCampaign(campaign);
        participation.setParticipant(participant);
        participation.setCurrentProgress(currentProgress);

        participation.setCurrentValue(currentValue);
        participation.setTargetValue(targetValue);

        return participation;
    }

    public static List<Participation> decodeParticipationList(JSONArray participationArrayJSON) throws JSONException, ParseException {
        if ((participationArrayJSON != null) && participationArrayJSON.length() > 0) {
            List<Participation> participationList = new ArrayList<>(participationArrayJSON.length());
            for (int p = 0; p < participationArrayJSON.length(); p++) {
                JSONObject pjson = participationArrayJSON.getJSONObject(p);
                Participation participation = decodeParticipation(pjson);
                participationList.add(participation);
            }
            return participationList;
        }else{
            return null;
        }
    }


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
        acc.setRelevanceScore(accountJSON.getInt("relevance"));
        return acc;
    }

}
