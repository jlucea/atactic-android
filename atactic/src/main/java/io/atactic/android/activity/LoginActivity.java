package io.atactic.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.atactic.android.R;
import io.atactic.android.datahandler.AuthenticationHandler;
import io.atactic.android.manager.ConfigurationManager;
import io.atactic.android.utils.CredentialsCache;

public class LoginActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

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
        CredentialsCache.UserCredentials recoveredCredentials = CredentialsCache.recoverCredentials();

        if (recoveredCredentials != null){
            Log.d(LOG_TAG, "Credentials found - Username: " + recoveredCredentials.getUserName());
            Log.v(LOG_TAG, "Attempting login with recovered credentials");

            usingRecoveredCredentials = true;
            handler = new AuthenticationHandler(this);
            handler.attemptLogin(recoveredCredentials.getUserName(),recoveredCredentials.getPassword());

        } else {
            // Initialize Login screen
            Log.v(LOG_TAG, "Credentials not found - Initializing login screen");
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

        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        // loginButton.setEnabled(true);

        emailTextField = findViewById(R.id.txt_email);
        passwordTextField = findViewById(R.id.txt_password);

        progressIndicator = findViewById(R.id.login_progress_bar);

        loginButton.setOnClickListener(v -> loginButtonPressed());
    }


    public void onAuthenticationSuccess(String username, String password, int userId, String token){

        Log.v(LOG_TAG, "Storing user credentials in app cache");

        // Store credentials for future reference throughout the app
        CredentialsCache.storeCredentials(username, password, userId);

        // Start configuration manager and request configuration
        Log.v(LOG_TAG, "Requesting configuration");
        ConfigurationManager.getInstance().requestUpdatedConfiguration(userId);

        // Launch Main Activity
        Log.v(LOG_TAG, "Entering app ...");
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);

        // End this activity
        finish();
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
            return true;
        }
        return false;
    }

}
