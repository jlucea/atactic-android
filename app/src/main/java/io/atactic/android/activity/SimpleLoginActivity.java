package io.atactic.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.atactic.android.R;
import io.atactic.android.connect.HttpRequestHandler;
import io.atactic.android.connect.LoginResponse;
import io.atactic.android.element.AtacticApplication;

public class SimpleLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailTextField;
    private EditText passwordTextField;

    private static final String LOG_TAG = SimpleLoginActivity.class.getSimpleName();

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

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                attemptLogin();
            }
        });
    }

    /*
     * Action performed when pressing the login button
     */
    public void attemptLogin(){

        // Disable button to prevent multiple clicks
        loginButton.setEnabled(false);

        String emailStr = emailTextField.getText().toString();
        String pwdStr = passwordTextField.getText().toString();

        Log.v(LOG_TAG,"Login button pressed: " + emailStr + ", " + pwdStr);
        Log.v(LOG_TAG,"Starting asynchronous execution of UserLoginTask");

        new UserLoginTask(emailStr,pwdStr).execute();
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     *
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        // private final String userToken;
        private int userId;

        private int error;
        private final static int ERR_NULL_RESPONSE = 10;
        private final static int ERR_WRONG_CREDENTIALS = 401;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.v("UserLoginTask", "Calling HttpRequestHandler.sendAuthenticationRequest");

            // Send Http request attempting login based on credentials
            LoginResponse response = HttpRequestHandler.sendAuthenticationRequest(mEmail,mPassword);

            if (response !=null ){
                Log.d("UserLoginTask", "Http response received");

                if (response.isOk()){
                    Log.d("UserLoginTask", "Http response received and OK");
                    try{
                        userId = Integer.parseInt(response.getContent());
                        Log.d("UserLoginTask", "User ID = " + userId);
                        return true;

                    }catch(NumberFormatException nfe){
                        Log.e("UserLoginTask","Error parsing user id from response", nfe);
                    }
                }else{
                    Log.d("UserLoginTask", "Http response received NOT ok");
                    error = ERR_WRONG_CREDENTIALS;
                }
            }else{
                Log.e(LOG_TAG,"NULL response from authentication service");
                error = ERR_NULL_RESPONSE;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.v("UserLoginTask", "Executing OnPostExecute after success");
                Log.d("UserLoginTask", "User ID = " + userId);

                ((AtacticApplication)SimpleLoginActivity.this.getApplication()).setUserId(userId);
                ((AtacticApplication)SimpleLoginActivity.this.getApplication()).setUserName(mEmail);
                ((AtacticApplication)SimpleLoginActivity.this.getApplication()).setPassword(mPassword);

                Log.v("UserLoginTask", "Launching next activity...");
                Intent i = new Intent(SimpleLoginActivity.this, QuestListActivity.class);
                startActivity(i);
            }else{
                Log.v("UserLoginTask", "onPostExecute failure scenario");
                loginButton.setEnabled(true);

                if (error == ERR_NULL_RESPONSE){
                    Toast.makeText(SimpleLoginActivity.this,
                            R.string.no_login_connection, Toast.LENGTH_LONG).show();
                }else if(error == ERR_WRONG_CREDENTIALS){
                    Toast.makeText(SimpleLoginActivity.this,
                            R.string.failed_login, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(SimpleLoginActivity.this,
                            R.string.unknown_error, Toast.LENGTH_SHORT).show();
                }

            }
        }

    }



}
