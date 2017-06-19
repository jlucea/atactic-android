package app.smartpath.android.smartpath.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import app.smartpath.android.smartpath.R;

public class QuestMapActivity extends FragmentActivity implements OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener {

    private GoogleMap map;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Add a marker and move the camera
        LatLng pos = new LatLng(41.64386, -0.90933);  // Zaragoza
        map.addMarker(new MarkerOptions().position(pos).title("My marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
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
