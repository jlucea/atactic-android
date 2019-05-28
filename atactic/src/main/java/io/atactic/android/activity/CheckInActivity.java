package io.atactic.android.activity;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;

import io.atactic.android.R;
import io.atactic.android.network.request.CheckInEligibleTargetsRequest;
import io.atactic.android.network.request.CheckInRequest;
import io.atactic.android.network.HttpResponse;
import io.atactic.android.utils.CredentialsCache;

public class CheckInActivity extends AppCompatActivity {

    private EditText commentsTextField;
    // private TextView userLocationTextView;
    private Spinner accountSpinnerView;
    private Button checkInButton;

    private float userLocationLatitude;
    private float userLocationLongitude;

    private static final String LOG_TAG = "CheckInActivity";

    private String[][] eligibleAccounts;

    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_check_in);
        super.onCreate(savedInstanceState);

        // Store local references to screen views
        checkInButton = findViewById(R.id.btn_checkIn);
        accountSpinnerView = findViewById(R.id.spinner_account);
        commentsTextField = findViewById(R.id.et_comments);
        // userLocationTextView = findViewById(R.id.tv_user_position);

        // Display back button in action bar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CredentialsCache.UserCredentials credentials = CredentialsCache.recoverCredentials();
        if (credentials != null) {
            userId = credentials.getUserId();
        } else {
            Log.wtf(LOG_TAG, "Unable to recover user credentials");
            finish();
        }

        /*
         * Add a click listener to the button that will call the checkIn method,
         *  but disable checkIn until the activity loads the accounts eligible for checkIn
         */
        checkInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                checkIn();
            }
        });
        checkInButton.setClickable(false);


        // Get user's location (latitude and longitude)
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);
        try {
            locationProvider.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // TODO Work with DOUBLE precision position
                                userLocationLongitude = (float)location.getLongitude();
                                userLocationLatitude = (float)location.getLatitude();
                                /*
                                userLocationTextView.setText(userLocationLatitude + " , "
                                    + userLocationLongitude);
                                */

                                Log.d(LOG_TAG,"User LATITUDE: " + userLocationLatitude);
                                Log.d(LOG_TAG,"User LONGITUDE: " + userLocationLongitude);

                                /*
                                 * Find accounts that are eligible for check-in:
                                 *      - They are close to the user's current location
                                 *      - The user hasn't checked in there in the las 60 mins
                                 *
                                 * and display the eligible account names and pre-select the closest one
                                 */
                                new EligibleTargetsHttpRequest().execute(userId);
                            }
                        }
                    });
        }catch(SecurityException se){
            Log.w(LOG_TAG, "Security exception caught.·);" +
                    " Device location will probably be unavailable.");
            Log.w(LOG_TAG, se);

            /*
             * Currently the app doesn't allow a user to check-in in his location is unavailable.
             *
             * TODO check if proximityRequirement for check-in is disabled. If it is, allow the user to report a non-geolocated check-in.
             *
             */
            Toast.makeText(CheckInActivity.this,
                    R.string.err_no_user_location, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    /**
     * Actions performed upon pressing the check-in button
     */
    public void checkIn(){
        /*
         * Gather parameters for the checkIn request: user id, account id, user comments
         * and current position.
         *
         *   - User id is stored as a global variable
         *   - Target account and comments are taken from the View
         *   - Current position was stored as a variable upon this Action's creation
         */
        int index = accountSpinnerView.getSelectedItemPosition();
        int accountId = Integer.parseInt(eligibleAccounts[index][0]);

        CheckInParams params = new CheckInParams(userId, accountId,
                commentsTextField.getText().toString(),
                userLocationLatitude,userLocationLongitude);

        // Execute Asynchronous Http POST request to report check-in
        new CheckInAsyncHttpRequest().execute(params);

        // Close screen and redirect to caller action
        this.finish();
    }

    /**
     * Encapsulates the parameters needed in a Http check-in request
     */
    private class CheckInParams{
        int userId;
        int accountId;
        String comments;
        float latitude;
        float longitude;

        CheckInParams(int userId, int accountId, String comments,
                             float latitude, float longitude) {

            this.userId = userId;
            this.accountId = accountId;
            this.comments = comments;
            this.latitude = latitude;
            this.longitude = longitude;
        }

    }


    /**
     * Perform an asynchronous Http POST request to report a check-in
     */
    public class CheckInAsyncHttpRequest extends AsyncTask<CheckInParams, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(CheckInParams... params) {
            // Send Http request and receive response
            HttpResponse response = CheckInRequest.send(
                    params[0].userId, params[0].accountId, params[0].comments,
                    params[0].latitude,params[0].longitude);

            if (response==null){
                Log.w("CheckInAsyncHttpRequest", "Check In response: null ");
            }else{
                Log.d("CheckInAsyncHttpRequest", "Check In response: " + response.getCode()
                        + " - " + response.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(HttpResponse checkInResponse) {
            if (checkInResponse.getCode() == HttpURLConnection.HTTP_OK){
                Toast.makeText(CheckInActivity.this,
                        R.string.msg_checkin_ok, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(CheckInActivity.this,
                        R.string.msg_checkin_error, Toast.LENGTH_LONG).show();
            }
        }
    }



    private class EligibleTargetsHttpRequest extends AsyncTask<Integer, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(Integer... params) {
            // Recover user's id from global variables
            // int userId = ((AtacticApplication)CheckInActivity.this.getApplication()).getUserId();
            int userId = params[0];

            // Send an Http request to the server asking for accounts eligible for checkin,
            // based on the user's location
            return CheckInEligibleTargetsRequest.send(userId,
                    userLocationLatitude, userLocationLongitude);
        }

        @Override
        protected void onPostExecute(JSONArray eligibleAccountsJsonArray) {
            if ((eligibleAccountsJsonArray != null) & (eligibleAccountsJsonArray.length()>0)){
                /*
                Toast.makeText(CheckInActivity.this,
                        "Found " + eligibleAccountsJsonArray.length()
                                + " accounts eligible for checkin",
                        Toast.LENGTH_LONG).show();
                */

                // Parse eligible accounts JSON block and populate spinner
                String[] accountDescriptors = parseAccounts(eligibleAccountsJsonArray);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        accountSpinnerView.getContext(),
                        R.layout.support_simple_spinner_dropdown_item);
                adapter.addAll(accountDescriptors);
                accountSpinnerView.setAdapter(adapter);

                // Activate the check-in button
                checkInButton.setClickable(true);
            }else{
                Toast.makeText(CheckInActivity.this,
                        R.string.err_no_eligible_accounts,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }


        /**
         * Parses the eligible accounts JSON array.
         * Stores the account names, ids and distance from user's location.
         * Returns an array containing the descriptions of the accounts parsed for displaying on
         * the GUI.
         *
         * @param accountsJSONArray JSON array of accounts to parse and display.
         * @return Array of account descripors in the form of "account_name (meters_away)"
         */
        private String[] parseAccounts(JSONArray accountsJSONArray){
            eligibleAccounts = new String[accountsJSONArray.length()][3];
            String[] accountDescriptors = new String[accountsJSONArray.length()];

            String accId, accName, metersAway;
            for (int i = 0; i < accountsJSONArray.length(); i++) {
                try {
                    accId = accountsJSONArray.getJSONObject(i).getString("id");
                    accName = accountsJSONArray.getJSONObject(i).getString("name");
                    metersAway = Integer.toString(Math.round(Float.parseFloat(
                            accountsJSONArray.getJSONObject(i).getString("distance"))));

                    eligibleAccounts[i][0] = accId;
                    eligibleAccounts[i][1] = accName;
                    eligibleAccounts[i][2] = metersAway;

                    accountDescriptors[i] = accName + " (" + metersAway + "m)";

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
            return  accountDescriptors;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "Options item selected");
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}
