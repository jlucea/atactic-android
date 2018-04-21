package io.atactic.android.network.request;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.atactic.android.network.LoginResponse;
import io.atactic.android.network.NetworkConstants;
import io.atactic.android.network.NetworkUtils;

public class LoginRequest {

    private static final String LOG_TAG = "LoginRequest";

    /**
     * Attempt login based on user credentials. Sends an URL-Encoded form through a POST Http request.
     *
     * Returns:
     *  - A LoginResponse with its content property filled with the ID
     *  of the authenticated user in case the authentication is successful.
     *  - A LoginResponse with response.ok = false and no content
     *  - NULL in case there's no connection
     *
     * @param username String representing the username (email)
     * @param password String containing the user's private password
     * @return LoginResponse object indicating the result of the operation
     */
    public static LoginResponse send(String username, String password){
        URL url = NetworkUtils.buildUrl(NetworkConstants.RSC_AUTH);
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

            int responseCode;
            try {
                // Get Http response values
                responseCode = connection.getResponseCode();
                Log.d(LOG_TAG, "Response code " + responseCode
                        + " for authentication of user " + username);

                response = new LoginResponse();
                response.setResponseCode(responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // This would launch an IOException if response code were greater than 400!
                    String responseMessage = NetworkUtils.readStreamContent(connection.getInputStream());
                    Log.d("AuthenticationRequest", "Response content: " + responseMessage);
                    response.setOk(true);
                    response.setContent(responseMessage);

                }else{
                    response.setOk(false);
                }
                return response;

            }catch (IOException ioe){
                Log.w(LOG_TAG, "Exception caught while getting Http response", ioe);
                Log.d(LOG_TAG, "Response code: " + connection.getResponseCode());
                response = new LoginResponse();
                response.setOk(false);
                response.setResponseCode(connection.getResponseCode());
                return response;

            } finally{
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception caught while connecting", e);
            return null;
        }
    }

}
