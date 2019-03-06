package io.atactic.android.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.activity.CheckInActivity;
import io.atactic.android.datahandler.MapDataHandler;
import io.atactic.android.datahandler.RouteGenerationHandler;
import io.atactic.android.manager.ConfigurationManager;
import io.atactic.android.manager.LocationManager;
import io.atactic.android.model.Account;
import io.atactic.android.model.ParticipationSummary;
import io.atactic.android.model.TargetAccount;
import io.atactic.android.model.TenantConfiguration;
import io.atactic.android.utils.CredentialsCache;

public class MapFragment extends Fragment implements
        OnMapReadyCallback, OnSuccessListener<Location>, MapDataHandler.MapDataPresenter,
        RouteGenerationHandler.RoutePresenter {

    private static final String LOG_TAG = MapFragment.class.getSimpleName();

    private GoogleMap map;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(LOG_TAG, "onCreate");
        Log.v(LOG_TAG, "Requesting location update");
        LocationManager.getInstance().updateLocation(this.getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment!= null) mapFragment.getMapAsync(this);

        // Enable options menu (tob bar buttons)
        setHasOptionsMenu(true);

        // Check tenant configuration
        // TenantConfiguration configuration = ConfigurationManager.getInstance().getConfiguration();
        /*
        FloatingActionButton myFab = view.findViewById(R.id.fab_checkin);
        if (configuration.isCheckInEnabled()) {
            // Activate the check-in floating button
            myFab.setVisibility(View.VISIBLE);
            myFab.setOnClickListener(this);
        } else {
            myFab.setVisibility(View.GONE);
        } */

        return view;
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

        Log.v(LOG_TAG, "onMapReady");

        map = googleMap;

        // Set up map
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        try {
            // This should display user's location as a blue dot icon and an accuracy radius
            map.setMyLocationEnabled(true);

            // Asynchronously request last location and execute onSuccess
            LocationManager.getInstance().getLastLocation(getContext(), this);

        } catch (SecurityException se) {
            Log.e(LOG_TAG, "SecurityException - Do we have user permission to enable location?", se);
            // TODO Promt user for permission
        }

        Log.v(LOG_TAG,"MapReady - Requesting data to MapDataHandler...");
        int userId = CredentialsCache.recoverCredentials(getContext()).getUserId();
        new MapDataHandler(this).getData(userId);
    }

    @Override
    public void onSuccess(Location location) {

        Log.v(LOG_TAG,"OnLocationSuccess - Center camera");

        if (location != null) {
            // Center camera to the user position using a specific zoom level
            LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
        }
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
        for (ParticipationSummary p : tgt.getParticipations()){
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

    public void displayMarkers(List<Account> accounts, List<TargetAccount> targets){

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Use the inflater's inflate method to inflate our top_menu_items layout to this top_menu_items
        inflater.inflate(R.menu.top_menu_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.btn_daily_route) {
            Log.v(LOG_TAG,"Route button pressed");

            int userId = CredentialsCache.recoverCredentials(getContext()).getUserId();
            Location location = LocationManager.getInstance().getLastKnownLocation();

            new RouteGenerationHandler(this).generateRoute(userId,
                    (float)location.getLatitude(), (float)location.getLongitude(),
                    5);

        } else if (item.getItemId() == R.id.btn_checkIn) {
            Log.v(LOG_TAG,"Check-in button pressed");
            openCheckIn();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayRoute(JSONArray waypointsJSON) {
        // Build Uri for Google Maps intent call
        Uri googleMapsUri = buildGoogleMapsUri(waypointsJSON,
                LocationManager.getInstance().getLastKnownLocation());

        launchMapsApp(googleMapsUri);
    }

    private void launchMapsApp(Uri uri){

        // Prepare intent
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);

        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(unrestrictedIntent);

            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getContext(), "Es necesario instalar una aplicación de mapas",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void displayRouteError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving");
    /**
     * Assumes number of waypoints > 2
     *
     * @param waypointsJSON json block describing the waypoints of the route
     * @param userLocation Current device location
     * @return Uri to open Maps
     */
    private Uri buildGoogleMapsUri(JSONArray waypointsJSON, Location userLocation){

        String mapsBaseUri = "https://www.google.com/maps/dir/?api=1";
        String originParam = "&origin=" + (float)userLocation.getLatitude() + "," + (float)userLocation.getLongitude();
        String waypointsParam = "&waypoints=";
        String destinationParam = "&destination=";
        final String travelModeParam = "&travelmode=driving";

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
            Log.e("buildGoogleMapsUri", err.getMessage());
            Toast.makeText(getContext(),"Se ha producido un error al generar la ruta", Toast.LENGTH_LONG).show();
            gmmIntentUri = null;
        }
        return gmmIntentUri;
    }

    private void openCheckIn(){
        Intent i = new Intent(getContext(), CheckInActivity.class);
        startActivity(i);
    }

}
