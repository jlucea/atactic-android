package app.smartpath.android.smartpath.activity;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;
import app.smartpath.android.smartpath.connect.HttpResponse;
import app.smartpath.android.smartpath.misc.SmartPathApplication;

public class CheckInActivity extends AppCompatActivity {

    private EditText commentsTextField;
    private TextView userlocationTextView;
    private Spinner accountSpinnerView;
    private Button checkInButton;

    private float userLocationLatitude;
    private float userLocationLongitude;

    private FusedLocationProviderClient locationProvider;

    private static final String LOG_TAG = "CheckInActivity";

    private String[][] eligibleAccounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        // Store local references to screen views
        checkInButton = (Button)findViewById(R.id.btn_checkIn);
        accountSpinnerView = (Spinner)findViewById(R.id.spinner_account);
        commentsTextField = (EditText)findViewById(R.id.et_comments);
        userlocationTextView = (TextView)findViewById(R.id.tv_user_position);

        // Add a click listener to the button that will call the checkIn method
        checkInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                checkIn();
            }
        });

        // Disable checkIn until the activity loads the accounts eligible for checkIn
        checkInButton.setClickable(false);

        // Get user's location (latitude and longitude)
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
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
                                userlocationTextView.setText(userLocationLatitude + " , "
                                    + userLocationLongitude);

                                Log.d(LOG_TAG,"User LATITUDE: " + userLocationLatitude);
                                Log.d(LOG_TAG,"User LONGITUDE: " + userLocationLongitude);

                                /*
                                 * Find accounts that are eligible for check-in:
                                 *      - They are close to the user's current location
                                 *      - The user hasn't checked in there in the las 60 mins
                                 *
                                 * and display the eligible account names and pre-select the closest one
                                 */
                                new EligibleTargetsHttpRequest().execute();
                            }
                        }
                    });
        }catch(SecurityException se){
            se.printStackTrace();
            // TODO Control cases where user rejects location permission
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
        int userId = ((SmartPathApplication)CheckInActivity.this.getApplication()).getUserId();

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

        public CheckInParams(int userId, int accountId, String comments,
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
            HttpResponse response = HttpRequestHandler.sendCheckInHttpRequest(
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



    public class EligibleTargetsHttpRequest extends AsyncTask<Void, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(Void... params) {
            // Recover user's id from global variables
            int userId = ((SmartPathApplication)CheckInActivity.this.getApplication()).getUserId();

            // Send an Http request to the server asking for accounts eligible for checkin,
            // based on the user's location
            return HttpRequestHandler.sendEligibleTargetsRequest(userId,
                    userLocationLatitude, userLocationLongitude);
        }

        @Override
        protected void onPostExecute(JSONArray eligibleAccountsJsonArray) {
            if (eligibleAccountsJsonArray != null) {
                /*
                Toast.makeText(CheckInActivity.this,
                        "Found " + eligibleAccountsJsonArray.length()
                                + " accounts eligible for checkin",
                        Toast.LENGTH_LONG).show();
                */

                // Parse eligible accounts JSON block and populate spinner
                String[] accountDescriptors = parseAccounts(eligibleAccountsJsonArray);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        accountSpinnerView.getContext(),
                        R.layout.support_simple_spinner_dropdown_item);
                adapter.addAll(accountDescriptors);
                accountSpinnerView.setAdapter(adapter);

                // Activate the check-in button
                checkInButton.setClickable(true);
            }else{
                Toast.makeText(CheckInActivity.this,
                        "No accounts eligible for checkin were found",
                        Toast.LENGTH_LONG).show();
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


}