package io.atactic.android.activity;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import io.atactic.android.R;
import io.atactic.android.network.request.ActiveTargetsRequest;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.element.TargetListAdapter;

public class PriorityListActivity extends AppCompatActivity {


    private RecyclerView targetListRecyclerView;

    private TargetListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority_list);

        /*
         * Initialize bottom navigation bar. Update click listener and mark selected item
         */
        BottomNavigationView bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setSelectedItemId(R.id.action_priorities);
        bottomNavigationBar.setOnNavigationItemSelectedListener(
                BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));


        // Get reference to the RecyclerView component
        targetListRecyclerView = findViewById(R.id.rv_target_list);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        targetListRecyclerView.setLayoutManager(layoutManager);
        targetListRecyclerView.setHasFixedSize(true);

        adapter = new TargetListAdapter();
        targetListRecyclerView.setAdapter(adapter);


        // Get user's location (latitude and longitude)
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);
        try {
            locationProvider.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                ActiveTargetsRequestParams params = new ActiveTargetsRequestParams(
                                        location.getLatitude(), location.getLongitude());
                                new ActiveTargetsAsyncHttpRequest().execute(params);
                                targetListRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }catch(SecurityException se) {
            Log.w("LOG_TAG", "Security exception caught.·);" +
                    " Device location will probably be unavailable.");
            Log.w("LOG_TAG", se);

            finish();
        }

    }



    private class ActiveTargetsRequestParams {
        double usrPosLat;
        double usrPosLon;

        ActiveTargetsRequestParams (double lat, double lon){
            usrPosLat = lat;
            usrPosLon = lon;
        }
    }


    class ActiveTargetsAsyncHttpRequest extends AsyncTask<ActiveTargetsRequestParams, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(ActiveTargetsRequestParams... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication) PriorityListActivity.this.getApplication()).getUserId();
            Log.d("ActiveTargetsRequest", "User ID: " + userId);

            float usrLat = (float) params[0].usrPosLat;
            float usrLon = (float) params[0].usrPosLon;

            // Send Http request and receive JSON response
            String response = ActiveTargetsRequest.send(userId, usrLat, usrLon);
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
        protected void onPostExecute(JSONArray targetArrayJSON) {
            adapter.setContent(targetArrayJSON);
            adapter.notifyDataSetChanged();
        }


    }



}
