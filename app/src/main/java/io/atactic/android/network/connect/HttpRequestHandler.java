package io.atactic.android.network.connect;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.LoginResponse;


public class HttpRequestHandler {

    // private static final String API_SERVER = "http://api.atactic.io";                           // Jelastic server
    private static final String API_SERVER = "http://env-6775033.jelastic.cloudhosted.es";   // Jelastic server
    // private static final String API_SERVER = "http://10.0.2.2:8080";                         // Emulating machine IP
    // private static final String API_SERVER = "http://192.168.1.37:8080";                     // Local server IP within WiFi network

    private static final String API_ROOT = "/mobile/rsc";

    private static final String RSC_AUTH = "/auth";
    private static final String RSC_QUESTS = "/quest";
    private static final String RSC_ACCOUNTS = "/account";
    private static final String RSC_CHECKIN = "/checkin";
    private static final String RSC_NEARBY_ACCOUNTS= "/account/nearby";
    private static final String RSC_TARGETS= "/target/u";
    private static final String RSC_QUEST_TARGETS= "/target/p";
    private static final String RSC_PROFILE = "/profile";
    private static final String RSC_RANKING = "/game/ranking";
    private static final String RSC_PATH= "/path/short";

    private static final String LOG_TAG = "HttpRequest";

    /**
     * Attempt login based on user credentials. Sends an URL-Encoded form through a POST Http request.
     *
     * Returns:
     *  - A LoginResponse with its content property filled with the ID
     *  of the authenticated user in case the authentication is successful.
     *  - A LoginResponse with response.ok = false and no content
     *  - NULL in case there's no connection
     *
     * @param username
     * @param password
     * @return
     */
    public static LoginResponse sendAuthenticationRequest(String username, String password){
        URL url = buildUrl(RSC_AUTH);
        Log.d(LOG_TAG,url.toString());

        LoginResponse response;

        try {
            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Get output data stream and write the request body
            OutputStream connectionOutputStream = connection.getOutputStream();
            DataOutputStream writer = new DataOutputStream(connectionOutputStream);
            String params = "username="+username+"&password="+password;
            writer.writeBytes(params);
            writer.flush();
            writer.close();

            int responseCode = 0;
            try {
                // Get Http response values
                responseCode = connection.getResponseCode();
                Log.d("AuthenticationRequest", "Response code " + responseCode
                        + " for authentication of user " + username);

                response = new LoginResponse();
                response.setResponseCode(responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // This would launch an IOException if response code were greater than 400!
                    String responseMessage = readStreamContent(connection.getInputStream());
                    Log.d("AuthenticationRequest", "Response content: " + responseMessage);
                    response.setOk(true);
                    response.setContent(responseMessage);

                }else{
                    response.setOk(false);
                }
                return response;

            }catch (IOException ioe){
                Log.w("AuthenticationRequest", "Exception caught while getting Http response", ioe);
                Log.d("AuthenticationRequest", "Response code: " + connection.getResponseCode());
                response = new LoginResponse();
                response.setOk(false);
                response.setResponseCode(connection.getResponseCode());
                return response;

            } finally{
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.e("AuthenticationRequest", "Exception caught while connecting", e);
            return null;
        }
    }


    /**
     *
     *
     * @param userId
     * @param accountId
     * @param comments
     * @param usrPosLatitude
     * @param usrPosLongitude
     */
    public static HttpResponse sendCheckInHttpRequest(int userId, int accountId, String comments,
                                              float usrPosLatitude, float usrPosLongitude){
        URL url = buildUrl(RSC_CHECKIN);
        Log.d(LOG_TAG,url.toString());

        try {
            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String params = "uid="+userId+"&account="+accountId+"&comments="+comments+"&lat="
                    +usrPosLatitude+"&lon="+usrPosLongitude;

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(params);
            writer.flush();
            writer.close();

            return new HttpResponse(connection.getResponseCode(),connection.getResponseMessage());

        }catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }


    /**
     * Request list of quests for a given user
     *
     * @param userId
     * @return
     */
    public static String sendQuestListRequest(int userId){
        URL url = buildUrl(RSC_QUESTS + "?uid=" + userId);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    /**
     * Returns OFF-TARGET accounts only.
     *
     * @param userId
     * @return JSON block
     */
    public static String sendAccountListRequest(int userId){
        URL url = buildUrl(RSC_ACCOUNTS
                + "?uid=" + userId
                + "&offtgtonly=true" );

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }


    public static String sendRequestForActiveTargets(int userId){
        URL url = buildUrl(RSC_TARGETS
                + "?uid=" + userId );

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    public static String sendRequestForActiveTargets(int userId, float usrPosLat, float usrPosLon){
        URL url = buildUrl(RSC_TARGETS
                + "?uid=" + userId
                + "&usrLat=" + usrPosLat
                + "&usrLon=" + usrPosLon);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }



    public static String sendRequestForParticipationargets(int userId, int participationId,
                                                   float usrPosLatitude, float usrPosLongitude){
        URL url = buildUrl(RSC_QUEST_TARGETS
                + "?uid=" + userId
                + "&pid=" + participationId
                + "&usrLat=" + usrPosLatitude
                + "&usrLon=" + usrPosLongitude);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }


    public static String sendRequestForRecommendedRoute(int userId, float usrLat, float usrLon, int waypoints){
        URL url = buildUrl(RSC_PATH
                + "?uid=" + userId
                + "&lat=" + usrLat
                + "&lon=" + usrLon
                + "&waypoints=" + waypoints);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return responseContent;
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }



    /**
     *
     * @param userId
     * @return
     */
    public static JSONObject sendUserProfileRequest(int userId){
        URL url = buildUrl(RSC_PROFILE + "?uid=" + userId);
        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());
                return new JSONObject(responseContent);
            }
        } catch(JSONException je) {
            je.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }


    public static JSONArray sendEligibleTargetsRequest(int userId, float usrPosLat, float usrPosLon){

        URL url = buildUrl(RSC_NEARBY_ACCOUNTS
                + "?uid=" + userId
                + "&usrLat=" + usrPosLat
                + "&usrLon=" + usrPosLon);

        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return new JSONArray(responseContent);
            }
        } catch(JSONException je) {
            je.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }


    public static JSONArray sendRankingRequest(int userId){
        URL url = buildUrl(RSC_RANKING + "?uid=" + userId);
        Log.d(LOG_TAG,url.toString());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                String responseContent = readStreamContent(urlConnection.getInputStream());

                return new JSONArray(responseContent);
            }
        } catch(JSONException je) {
            je.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }


    /**
     * Utility function to get text data from a stream.
     *
     * @param in
     * @return
     */
    private static String readStreamContent(InputStream in){
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\A");

        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }

    /**
     * Creates an URL for the specified API resource
     *
     * @param resource_uri
     * @return
     */
    private static URL buildUrl(String resource_uri){
        String baseUrl = API_SERVER.concat(API_ROOT).concat(resource_uri);
        Uri uri = Uri.parse(baseUrl).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
