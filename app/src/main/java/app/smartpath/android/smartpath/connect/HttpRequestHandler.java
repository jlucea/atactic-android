package app.smartpath.android.smartpath.connect;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class HttpRequestHandler {

    private static final String API_SERVER = "http://10.0.2.2:8080";  // "http://localhost:8080";
    private static final String API_ROOT = "/smartpath/api";

    private static final String RSC_AUTH = "/auth";
    private static final String RSC_QUESTS = "/quest";
    private static final String RSC_ACCOUNTS = "/account";
    private static final String RSC_CHECKIN = "/activity";
    private static final String RSC_TARGETS= "/target/query";
    private static final String RSC_PROFILE = "/profile";

    private static final String LOG_TAG = "HttpRequest";

    /**
     * Attempt login based on user credentials
     *
     * @param username
     * @param password
     * @return
     */
    public static LoginResponse sendAuthenticationRequest(String username, String password){
        URL url = buildUrl(RSC_AUTH);
        Log.d(LOG_TAG,url.toString());

        try {
            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String params = "username="+username+"&password="+password;

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(params);
            writer.flush();
            writer.close();

            // Get Http response values
            int responseCode = connection.getResponseCode();
            String responseMessage = readStreamContent(connection.getInputStream());

            Log.v("AuthenticationRequest", "Username: "+username);
            Log.v("AuthenticationRequest", "Response code: "+responseCode);
            Log.v("AuthenticationRequest", "Response content: "+responseMessage);

            // Build and return response object
            LoginResponse response = new LoginResponse();
            response.setResponseCode(responseCode);
            response.setOk(responseCode==HttpURLConnection.HTTP_OK);
            response.setContent(responseMessage);

            return response;

        } catch (IOException e) {
            e.printStackTrace();
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
        URL url = buildUrl(RSC_QUESTS + "/" + userId);
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
     * @return JSON block
     */
    public static String sendAccountListRequest(int userId){
        URL url = buildUrl(RSC_ACCOUNTS + "/" + userId);
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
        URL url = buildUrl(RSC_PROFILE + "/" + userId);
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

        URL url = buildUrl(RSC_TARGETS
                + "?userId=" + userId
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
