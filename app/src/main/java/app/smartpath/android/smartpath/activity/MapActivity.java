package app.smartpath.android.smartpath.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

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

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;
import app.smartpath.android.smartpath.misc.BottomNavigationBarClickListenerFactory;
import app.smartpath.android.smartpath.misc.SmartPathApplication;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient locationProvider;

    private static final int ZOOM_LEVEL = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize views
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        this.setActionBar(myToolbar);

        /*
         * Get the reference to the bottom navigation bar. Update click listener and selected item
         */
        BottomNavigationView bottomNavigationBar = (BottomNavigationView)findViewById(R.id.bottom_navigation);
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

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab_checkin);
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

        // Retrieve user location, draw the corresponding marker and center map
        try {
            map.setMyLocationEnabled(true);
            locationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng usrLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    BitmapDescriptor usrIcon =
                            BitmapDescriptorFactory.fromResource(R.drawable.marker_user_42);

                    // BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)

                    map.addMarker(new MarkerOptions()
                            .position(usrLatLng)
                            .icon(usrIcon));

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(usrLatLng, ZOOM_LEVEL));
                }
            });

        }catch (SecurityException se){
            // TODO Ask for permission, or center map in some account
            se.printStackTrace();
        }

        // Send an asynchronous request to get the account list
        new AccountsAsyncHttpRequest().execute();

    }


    /**
     * This class' execute method sends an asynchronous http request to the server
     * and assigns the returning JSON Array to the private variable accounts.
     *
     */
    public class AccountsAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((SmartPathApplication)MapActivity.this.getApplication()).getUserId();
            Log.d("AccountsHttpRequest", "User ID: " + userId);

            // Send Http request and receive JSON response
            String response = HttpRequestHandler.sendAccountListRequest(userId);
            Log.d("AccountsHttpRequest", "JSON Response: " + response);

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

        /**
         *
         * @param accounts
         */
        private void drawAccountMarkers(JSONArray accounts){
            Log.d("drawAccountMarkers", accounts.length() + " markers to draw");
            try {
                // Iterate through JSON Array objects (accounts)
                for(int i=0; i<accounts.length(); i++) {

                    // Parse account information to display from JSON array
                    JSONObject accountObj = accounts.getJSONObject(i);
                    int accId = accountObj.getInt("id");
                    String accName =accountObj.getString("name");
                    LatLng accPos = new LatLng(accountObj.getDouble("latitude"),
                            accountObj.getDouble("longitude"));
                    JSONArray participations = accountObj.getJSONArray("participations");

                    Log.d("drawAccountMarkers", "drawing marker for account " + accId
                            + " at ("+accPos.latitude+" , " + accPos.longitude + ")");
                    Log.d("drawAccountMarkers", "account " + accId + " will trigger " +
                            participations.length() + " quests");

                    // Set up the marker options
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(accPos)
                            .title(accName);

                    BitmapDescriptor markerIcon;
                    if (participations.length()>0){
                        // Set icon for highlighted targets
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_important2_36);

                        // Set up snippet text for highlighted targets
                        String snippetText = "";
                        for (int j=0; j < participations.length(); j++){
                            String questName = participations.getJSONObject(j)
                                    .getJSONObject("campaign").getString("name");

                            Log.d("drawAccountMarkers", "Quest name: " + questName);
                            snippetText = snippetText + " - " + questName;
                        }
                        markerOptions.snippet(snippetText);
                    }else{
                        // Default marker icon
                        // markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_account_24);
                    }
                    markerOptions.icon(markerIcon);

                    // Add the marker to the map
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
