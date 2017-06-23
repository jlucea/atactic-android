package app.smartpath.android.smartpath.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;
import app.smartpath.android.smartpath.misc.SmartPathApplication;

public class QuestMapActivity extends FragmentActivity implements OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener {

    private JSONArray accounts;

    private GoogleMap map;
    private BottomNavigationView navigationView;
    private FusedLocationProviderClient locationProvider;

    private static final int ZOOM_LEVEL = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize views
        setContentView(R.layout.activity_quest_map);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        this.setActionBar(myToolbar);

        /*
         * Get the reference to the bottom navigation bar and add this class as
         * ItemSelectedListener
         */
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get user location provider
        locationProvider = LocationServices.getFusedLocationProviderClient(this);

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
                    map.addMarker(new MarkerOptions()
                            .position(usrLatLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_user_marker)));

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
            int userId = ((SmartPathApplication)QuestMapActivity.this.getApplication()).getUserId();
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
                for(int i=0; i<accounts.length(); i++) {
                    JSONObject accountObj = accounts.getJSONObject(i);
                    int accId = accountObj.getInt("id");
                    String accName =accountObj.getString("name");
                    LatLng accPos = new LatLng(accountObj.getDouble("latitude"),
                            accountObj.getDouble("longitude"));

                    Log.d("drawAccountMarkers", "drawing marker for account " + accId
                            + " at ("+accPos.latitude+" , " + accPos.longitude + ")");

                    map.addMarker(new MarkerOptions()
                            .position(accPos)
                            .title(accId + ": "+accName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }

            }catch (JSONException jsonerr){
                Log.e("drawAccountMarkers", jsonerr.getMessage());
            }
        }


    }


    /**
     * Defines the behavior of the bottom navigation bar of this section.
     *
     * However, the right way to do this would be to create a component that extended
     * the BottomNavigationBar class and included an OnNavigationItemSelectedListener that
     * could navigate between FRAGMENTS.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_quests:
                // Toast.makeText(this, "Quests icon clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(QuestMapActivity.this, QuestListActivity.class);
                startActivity(i);
                return true;
        }
        return true;
    }



}
