package io.atactic.android.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.manager.MapDataHandler;
import io.atactic.android.model.Account;
import io.atactic.android.model.AccountMap;
import io.atactic.android.model.Participation;
import io.atactic.android.model.TargetAccount;
import io.atactic.android.network.request.AccountListRequest;
import io.atactic.android.network.request.ActiveTargetsRequest;
import io.atactic.android.network.request.RecommendedRouteRequest;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.element.AtacticApplication;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient locationProvider;

    private static final int ZOOM_LEVEL = 16;

    private static final String LOG_TAG = "MapActivity";

    private LatLng userLocation;


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
                Intent i = new Intent(MapActivity.this, CheckInActivity.class);
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

            locationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM_LEVEL));
                    } else {
                        Log.w(LOG_TAG, "User location is NULL");
                    }
                }

            });


        } catch (SecurityException se) {
            // TODO Promt user for permission
            se.printStackTrace();
        }

        System.out.println("MapReady - Requesting data to MapDataHandler...");
        new MapDataHandler(this).getData();

        // Send an asynchronous request to get non-target accounts
        // new OffTargetAccountsAsyncHttpRequest().execute();

        // Send an asynchronous request to get live targets
        // new ActiveTargetsAsyncHttpRequest().execute();
    }


    private void drawAccountMarker(Account acc){
        LatLng position = new LatLng(acc.getLatitude(), acc.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(acc.getName());

        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(
                R.drawable.marker_neutral_24g);

        // Add the marker to the map
        markerOptions.icon(markerIcon);
        map.addMarker(markerOptions);
    }


    private void drawTargetMarker(TargetAccount tgt){

        String snippetText = "";
        for (Participation p : tgt.getParticipations()){
            snippetText = snippetText.concat(p.getCampaignName() + " ");
        }

        LatLng position = new LatLng(tgt.getLatitude(), tgt.getLongitude());

        // Set up  marker options: position, title, icon and snippet
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(tgt.getName())
                .snippet(snippetText)
                .icon(BitmapDescriptorFactory.fromResource(
                        R.drawable.marker_important_32));

        // Add the marker to the map
        map.addMarker(markerOptions);
    }


    public void drawMarkers(List<Account> accounts, List<TargetAccount> targets){

        if (accounts!= null) {
            Log.d(LOG_TAG, "Will draw " + accounts.size() + " account markers");
            for (Account acc : accounts) {
                drawAccountMarker(acc);
            }
        }

        if (targets != null) {
            Log.d(LOG_TAG, "Will draw " + targets.size() + " target markers");
            for (TargetAccount target: targets) {
                drawTargetMarker(target);
            }
        }
    }


    public class GeneratePathAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        private int NUMBER_OF_WAYPOINTS = 4;

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication) MapActivity.this.getApplication()).getUserId();
            Log.d("GeneratePathAsync", "User ID: " + userId);

            // Send Http request and receive JSON response
            String response = RecommendedRouteRequest.send(userId,
                    (float)userLocation.latitude, (float)userLocation.longitude, NUMBER_OF_WAYPOINTS);

            Log.d("GeneratePathAsync", "JSON Response: " + response);

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
            // Build Uri for Google Maps intent call
            Uri googleMapsUri = buildGoogleMapsUri(JSONResponse);

            if (googleMapsUri != null) {
                // Prepare intent
                Intent intent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
                intent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(intent);

                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);
                        startActivity(unrestrictedIntent);

                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(getBaseContext(), "Please install a maps application",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

        }

        // Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving");
        /**
         * Currently assuming number of waypoints > 2
         *
         * @param waypointsJSON
         * @return
         */
        private Uri buildGoogleMapsUri(JSONArray waypointsJSON){

            String mapsBaseUri = "https://www.google.com/maps/dir/?api=1";
            String originParam = "&origin=" + (float)userLocation.latitude + "," + (float)userLocation.longitude;
            String waypointsParam = "&waypoints=";
            String destinationParam = "&destination=";
            String travelModeParam = "&travelmode=driving";

            Uri gmmIntentUri;
            try {
                for (int i = 0; i < waypointsJSON.length(); i++) {
                    double lat = waypointsJSON.getJSONObject(i).getDouble("latitude");
                    double lon = waypointsJSON.getJSONObject(i).getDouble("longitude");
                    if (i == waypointsJSON.length() - 1) {
                        destinationParam += lat + "," + lon;
                    } else {
                        waypointsParam += lat + "," + lon;
                        if (i < waypointsJSON.length() - 2) waypointsParam += "|";
                    }
                }

                gmmIntentUri = Uri.parse(mapsBaseUri + originParam + destinationParam + waypointsParam + travelModeParam);
                Log.d("buildGoogleMapsUri", "URI =" + gmmIntentUri.toString());

            }catch (JSONException err){
                Log.e("GeneratePathAsync", err.getMessage());
                Toast.makeText(getBaseContext(),"Se ha producido un error al calcular la ruta", Toast.LENGTH_LONG).show();
                gmmIntentUri = null;
            }
            return gmmIntentUri;
        }

    }


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

        if (id == R.id.btn_daily_route) {
            /*
            Toast.makeText(MapActivity.this,
                    "Route button pressed", Toast.LENGTH_SHORT).show();
            */
            Log.v("MapActivity","Route button pressed");

            if (userLocation != null) {

                String usrPosLong = String.valueOf(userLocation.longitude);
                String usrPosLat = String.valueOf(userLocation.latitude);

                /*
                Toast.makeText(MapActivity.this,
                        "Long=" + usrPosLong + ", Lat=" + usrPosLat, Toast.LENGTH_SHORT).show();
                */
                Log.d("MapActivity","User location: " + "Long=" + usrPosLong + ", Lat=" + usrPosLat);

                new GeneratePathAsyncHttpRequest().execute();

            }else{
                Toast.makeText(MapActivity.this,
                        "User location unknown", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
