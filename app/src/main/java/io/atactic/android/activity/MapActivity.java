package io.atactic.android.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.atactic.android.R;
import io.atactic.android.connect.HttpRequestHandler;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.element.AtacticApplication;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient locationProvider;

    private static final int ZOOM_LEVEL = 16;

    private static final String LOG_TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        /*
         * Get the reference to the bottom navigation bar. Update click listener and selected item
         */
        BottomNavigationView bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setSelectedItemId(R.id.action_map);
        bottomNavigationBar.setOnNavigationItemSelectedListener(
            BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get user location provider
        locationProvider = LocationServices.getFusedLocationProviderClient(this);

        // Activate the check-in floating button
        FloatingActionButton myFab = findViewById(R.id.fab_checkin);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this,CheckInActivity.class);
                startActivity(i);
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Set up map
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        // Retrieve user location, draw the corresponding marker and center map
        try {
            // This should display user's location as a blue dot icon and an accuracy radius
            map.setMyLocationEnabled(true);

            // We can also add a custom marker representing user's position
            locationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location!=null) {
                        LatLng usrLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        /*
                        BitmapDescriptor usrIcon =
                                BitmapDescriptorFactory.fromResource(R.drawable.marker_user_42);
                        map.addMarker(new MarkerOptions()
                                .position(usrLatLng)
                                .icon(usrIcon));
                        */
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(usrLatLng, ZOOM_LEVEL));
                    }else{
                        Log.w(LOG_TAG,"User location is NULL");
                    }
                }
            });

        }catch (SecurityException se){
            // TODO Promt user for permission
            se.printStackTrace();
        }

        // Send an asynchronous request to get non-target accounts
        new OffTargetAccountsAsyncHttpRequest().execute();

        // Send an asynchronous request to get live targets
        new ActiveTargetsAsyncHttpRequest().execute();
    }




    /**
     * This class' execute method sends an asynchronous http request to the server
     * and draws Account markers on post-execute
     */

    public class ActiveTargetsAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication) MapActivity.this.getApplication()).getUserId();
            Log.d("ActiveTargetsRequest", "User ID: " + userId);

            // Send Http request and receive JSON response
            String response = HttpRequestHandler.sendRequestForActiveTargets(userId);
            Log.d("ActiveTargetsRequest", "JSON Response: " + response);

            // Return JSON array containing the data to show in the view
            try {
                return new JSONArray(response);

            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray JSONResponse) {
            drawAccountMarkers(JSONResponse);
        }

        /**
         * @param targets
         */
        private void drawAccountMarkers(JSONArray targets) {

            String snippetText ="";
            for (int j=0; j < targets.length(); j++) {
                try {
                    JSONObject tgt = targets.getJSONObject(j);
                    JSONObject account = tgt.getJSONObject("account");
                    JSONObject participation = tgt.getJSONObject("participation");
                    JSONObject campaign = participation.getJSONObject("campaign");

                    int accId = account.getInt("id");
                    String accName = account.getString("name");
                    LatLng accPos = new LatLng(
                            account.getDouble("latitude"),
                            account.getDouble("longitude"));

                    String qName = campaign.getString("name");
                    /*
                    String qStartDate = campaign.getString("startDate").split("T")[0];
                    String qEndDate = campaign.getString("endDate").split("T")[0];
                    */

                    boolean drawMarker = true;
                    if (j<targets.length()-1) {
                        int nextAccId = targets.getJSONObject(j+1).getJSONObject("account").getInt("id");
                        if (accId != nextAccId) {
                            // Draw Marker
                            snippetText += qName;
                        } else {
                            // Don't draw marker yet: edit snippet text and continue iteration
                            drawMarker = false;
                            snippetText += qName + "\n";
                        }
                    }
                    if (drawMarker){
                        // Set up  marker options: position, title, icon and snippet
                        MarkerOptions markerOptions = new MarkerOptions()
                            .position(accPos)
                            .title(accName)
                            .snippet(snippetText)
                            .icon(BitmapDescriptorFactory.fromResource(
                                    R.drawable.marker_important_32));

                        // Add the marker to the map
                        map.addMarker(markerOptions);
                        snippetText = "";
                    }

                } catch (JSONException jsonerr) {
                    jsonerr.printStackTrace();
                }
            }// End of loop
        }

    }








    /**
     * This class' execute method sends an asynchronous http request to the server
     * and draws Account markers on post-execute
     */
    public class OffTargetAccountsAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication)MapActivity.this.getApplication()).getUserId();
            Log.d("OffTgtAccountsRequest", "User ID: " + userId);

            // Send Http request and receive JSON response
            String response = HttpRequestHandler.sendAccountListRequest(userId);
            Log.d("OffTgtAccountsRequest", "JSON Response: " + response);

            // Return JSON array containing the data to show in the view
            try {
                return new JSONArray(response);

            }catch (JSONException ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray JSONResponse) {
            drawAccountMarkers(JSONResponse);
        }


        private void drawAccountMarkers(JSONArray accounts){
            Log.d("drawAccountMarkers", accounts.length() + " markers to draw");
            try {
                // Iterate through JSON Array objects (accounts)
                for(int i=0; i<accounts.length(); i++) {

                    // Parse account information to display from JSON array
                    JSONObject accountObj = accounts.getJSONObject(i);
                    // int accId = accountObj.getInt("id");
                    String accName = accountObj.getString("name");
                    LatLng accPos = new LatLng(
                            accountObj.getDouble("latitude"),
                            accountObj.getDouble("longitude"));

                    // Set up  marker options: position and title
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(accPos)
                            .title(accName);

                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(
                            R.drawable.marker_neutral_24g);

                    // Add the marker to the map
                    markerOptions.icon(markerIcon);
                    map.addMarker(markerOptions);
                }

            }catch (JSONException jsonerr){
                Log.e("drawAccountMarkers", jsonerr.getMessage());
            }
        }

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Use AppCompatActivity's method getMenuInflater to get a handle on the top_menu_items inflater
        MenuInflater inflater = getMenuInflater();
        // Use the inflater's inflate method to inflate our top_menu_items layout to this top_menu_items
        inflater.inflate(R.menu.top_menu_items, menu);
        // Return true so that the top_menu_items is displayed in the Toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_button) {
            Intent i = new Intent(MapActivity.this, ProfileActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
