package io.atactic.android.activity;

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

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailTextField;
    private EditText passwordTextField;
    private ProgressBar progressIndicator;

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        // Here we could remove the notification bar for a spectacular, full-screen login
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

    /*
     * Action performed when pressing the login button
     */
    public void loginButtonPressed(){

        // Disable button to prevent multiple clicks
        loginButton.setEnabled(false);
        loginButton.setBackgroundColor(getResources().getColor(R.color.atactic_medium_gray, null));

        // Show progress indicator
        progressIndicator.setVisibility(View.VISIBLE);

        String emailStr = emailTextField.getText().toString();
        String pwdStr = passwordTextField.getText().toString();

        Log.v(LOG_TAG,"Login button pressed: " + emailStr + ", " + pwdStr);
        Log.v(LOG_TAG,"Starting asynchronous execution of UserLoginAsyncTask");

        new AuthenticationHandler(this).attemptLogin(emailStr,pwdStr);
    }

    public void displayErrorMessage(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void resetViews(){
        loginButton.setEnabled(true);
        loginButton.setBackgroundColor(getResources().getColor(R.color.atactic_bright_red, null));
        progressIndicator.setVisibility(View.GONE);
    }

}
