package io.atactic.android.datahandler;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import io.atactic.android.network.request.UserProfileRequest;

/**
 * Gets user profile information for a presenter to display
 *
 * @author Jaime Lucea
 * @author ATACTIC
 */
public class ProfileManager {

    private ProfileDataPresenter presenter;

    public ProfileManager(ProfileDataPresenter profileDataPresenter){
        this.presenter = profileDataPresenter;
    }

    public void getData(int userId){
        // Execute asynchronous request
        new UserProfileAsyncHttpRequest(this).execute(userId);
    }

    private void presentData(String name, String role, String score){
        presenter.displayData(name, role, score);
    }

    private void presentMessage(String message){
        presenter.displayMessage(message);
    }

    /**
     * Asynchronous HTTP data request
     */
    public static class UserProfileAsyncHttpRequest extends AsyncTask<Integer, Void, JSONObject> {

        private ProfileManager profileManager;

        UserProfileAsyncHttpRequest(ProfileManager manager){
            this.profileManager = manager;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {

            // Retrieve user's id from parameters
            int userId = params[0];

            // Send UserProfileRequest
            return UserProfileRequest.send(userId);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                String fullName = jsonObject.getString("firstName") + " "
                        + jsonObject.getString("lastName");
                String role = jsonObject.getString("position");
                String scoreStr = jsonObject.getString("score");

                profileManager.presentData(fullName, role, scoreStr);

            }catch (JSONException err){
                // TODO Log error
                profileManager.presentMessage("Error al leer los datos de perfil de usuario");
            }
        }
    }

    public interface ProfileDataPresenter {
        void displayData(String fullUserName, String userPosition, String userScore);
        void displayMessage(String message);
    }


}
