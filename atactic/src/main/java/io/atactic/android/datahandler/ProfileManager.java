package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import io.atactic.android.network.HttpResponse;
import io.atactic.android.network.request.UserProfileRequest;
import io.atactic.android.utils.CredentialsCache;

/**
 * Gets user profile information for a profile presenter to display
 *
 * @author Jaime Lucea
 * @author ATACTIC
 */
public class ProfileManager {

    private static final String LOG_TAG = "ProfileManager";

    private ProfileDataPresenter presenter;


    public interface ProfileDataPresenter {
        void displayData(String fullUserName, String userPosition, String userScore);
        void displayMessage(String message);
    }

    public ProfileManager(ProfileDataPresenter profileDataPresenter){
        this.presenter = profileDataPresenter;
    }

    public void getData(){
        CredentialsCache.UserCredentials credentials =  CredentialsCache.recoverCredentials();
        if (credentials != null) {
            int userId = credentials.getUserId();

            // Execute asynchronous request
            new UserProfileAsyncHttpRequest(this).execute(userId);

        } else {
            Log.wtf(LOG_TAG, "Could not recover user credentials");
        }
    }


    private void handleResponse(HttpResponse response) {

        if (response != null) {
            if (response.getCode() == HttpURLConnection.HTTP_OK) {
                try {
                    JSONObject profileJSON = new JSONObject(response.getContent());
                    String fullName =
                              profileJSON.getString("firstName") + " "
                            + profileJSON.getString("lastName");
                    String role = profileJSON.getString("position");
                    String scoreStr = profileJSON.getString("score");

                    presenter.displayData(fullName, role, scoreStr);

                }catch (JSONException err){
                    Log.e(LOG_TAG, "Error while decoding profile data", err);
                    presenter.displayMessage("Error al leer los datos de perfil de usuario");
                }
            } else {
                Log.e(LOG_TAG, "Server error - " + response.getCode());
                presenter.displayMessage("Se ha producido un error en el servidor al consultar el perfil de usuario");
            }
        } else {
            Log.e(LOG_TAG,"No response from server");
            presenter.displayMessage("Se ha perdido la conexión con el servidor");
        }

    }


    /**
     * Asynchronous HTTP data request
     */
    public static class UserProfileAsyncHttpRequest extends AsyncTask<Integer, Void, HttpResponse> {

        private ProfileManager profileManager;

        UserProfileAsyncHttpRequest(ProfileManager manager){
            this.profileManager = manager;
        }

        @Override
        protected HttpResponse doInBackground(Integer... params) {
            return UserProfileRequest.send(params[0]);
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            profileManager.handleResponse(response);
        }
    }


}
