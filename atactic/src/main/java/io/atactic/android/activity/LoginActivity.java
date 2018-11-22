package io.atactic.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.atactic.android.R;
import io.atactic.android.datahandler.AuthenticationHandler;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.manager.ConfigurationManager;
import io.atactic.android.utils.CredentialsCache;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailTextField;
    private EditText passwordTextField;
    private ProgressBar progressIndicator;

    private AuthenticationHandler handler;

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private boolean usingRecoveredCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Try to recover credentials from Application cache
        CredentialsCache.UserCredentials recoveredCredentials = CredentialsCache.recoverCredentials(this);

        if (recoveredCredentials != null){
            Log.d(LOG_TAG, "Credentials found - Username: " + recoveredCredentials.getUserName());
            Log.v(LOG_TAG, "Attempting login with recovered credentials");

            usingRecoveredCredentials = true;
            handler = new AuthenticationHandler(this);
            handler.attemptLogin(recoveredCredentials.getUserName(),recoveredCredentials.getPassword());

        } else {
            // Initialize Login screen
            Log.v(LOG_TAG, "Initializing login screen");
            usingRecoveredCredentials = false;
            initialize();
        }
    }

    private void initialize(){

        // Here we could remove the notification bar for a spectacular, full-screen login
        /*
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */

        setContentView(R.layout.activity_simple_login);

        loginButton = findViewById(R.id.login_button);
        loginButton.setEnabled(true);

        emailTextField = findViewById(R.id.txt_email);
        passwordTextField = findViewById(R.id.txt_password);

        progressIndicator = findViewById(R.id.login_progress_bar);

        loginButton.setOnClickListener(v -> loginButtonPressed());
    }


    public void onAuthenticationSuccess(String username, String password, int userId, String token){

        Log.v(LOG_TAG, "Storing user credentials in app cache");

        // Store credentials as a global application variable, so it can be referenced from other activities
        ((AtacticApplication)getApplication()).storeUserCredentialsInAppCache(userId, username, password);

        // Store in Preferences for future authentication
        CredentialsCache.storeCredentials(this, username, password, userId, token);

        // Start configuration manager and request configuration
        Log.v(LOG_TAG, "Requesting configuration");
        ConfigurationManager.getInstance().requestUpdatedConfiguration(userId);

        Log.v(LOG_TAG, "Entering app ...");
        Intent i = new Intent(LoginActivity.this, CampaignListActivity.class);
        startActivity(i);
    }

    public void onAuthenticationFail(String message){
        if (usingRecoveredCredentials){
            initialize();
        } else {
            resetViews();
            displayErrorMessage(message);
        }
    }

    private void displayErrorMessage(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /*
     * Action performed when pressing the login button
     */
    public void loginButtonPressed(){

        usingRecoveredCredentials = false;

        // Disable button to prevent multiple clicks
        loginButton.setEnabled(false);
        loginButton.setBackgroundColor(getResources().getColor(R.color.atactic_medium_gray, null));

        // Show progress indicator
        progressIndicator.setVisibility(View.VISIBLE);

        String emailStr = emailTextField.getText().toString();
        String pwdStr = passwordTextField.getText().toString();

        Log.v(LOG_TAG,"Login button pressed: " + emailStr + ", " + pwdStr);
        Log.v(LOG_TAG,"Starting asynchronous execution of UserLoginAsyncTask");

        if (handler == null) handler = new AuthenticationHandler(this);
        handler.attemptLogin(emailStr,pwdStr);
    }


    private void resetViews(){
        loginButton.setEnabled(true);
        loginButton.setBackgroundColor(getResources().getColor(R.color.atactic_bright_red, null));
        progressIndicator.setVisibility(View.GONE);
    }

}
