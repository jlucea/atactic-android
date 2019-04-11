package io.atactic.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.AccountParticipationsDataHandler;
import io.atactic.android.element.SimpleParticipationListAdapter;
import io.atactic.android.model.Account;
import io.atactic.android.model.Participation;
import io.atactic.android.utils.CredentialsCache;
import io.atactic.android.utils.DistanceUtils;

public class AccountDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Account account;

    private SimpleParticipationListAdapter adapter;
    private RecyclerView participationListRecyclerView;

    private FrameLayout loadingIndicatorLayout;
    private FrameLayout statusMessageLayout;
    private TextView statusMessageTextView;

    private static final int ZOOM_LEVEL = 13;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        // Display back button in action bar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingIndicatorLayout = findViewById(R.id.account_campaigns_loading_indicator);
        loadingIndicatorLayout.setVisibility(View.VISIBLE);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.account_map);

        if (mapFragment != null) mapFragment.getMapAsync(this);

        // Get account data from caller intent
        account = getAccountFromIntent(getIntent());

        // Load view references
        TextView accountNameTextView = findViewById(R.id.tv_account_name);
        TextView accountTypeTextView = findViewById(R.id.tv_account_type);
        TextView accountAddressLine1TextView = findViewById(R.id.tv_account_address_line1);
        TextView accountAddressLine2TextView = findViewById(R.id.tv_account_address_line2);
        TextView distanceToAccountTextView = findViewById(R.id.tv_distance_to_account);
        TextView relevanceScoreTextView = findViewById(R.id.tv_account_priority);
        statusMessageTextView = findViewById(R.id.tv_status_message);
        statusMessageLayout = findViewById(R.id.status_message_layout);
        participationListRecyclerView = findViewById(R.id.rv_account_campaigns);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        participationListRecyclerView.setLayoutManager(layoutManager);
        participationListRecyclerView.setHasFixedSize(true);
        adapter = new SimpleParticipationListAdapter(null);     // TODO Add click listener
        participationListRecyclerView.setAdapter(adapter);

        // Display account data
        accountNameTextView.setText(account.getName());
        accountTypeTextView.setText(account.getType());
        accountAddressLine1TextView.setText(account.getAddress());
        accountAddressLine2TextView.setText(account.getPostalCode() + ", " + account.getCity());
        distanceToAccountTextView.setText(DistanceUtils.formatDistanceText(account.getDistanceTo()));
        relevanceScoreTextView.setText(String.valueOf(account.getRelevanceScore()));

        int userId = CredentialsCache.recoverCredentials(this).getUserId();

        // Request list of campaigns related to the account
        new AccountParticipationsDataHandler(this).getData(userId, account.getId());
    }


    private Account getAccountFromIntent(Intent intent){

        int accountId = intent.getIntExtra("accountId", 0);
        String accountName = intent.getStringExtra("accountName");
        String accountType = intent.getStringExtra("accountType");
        String accountAddress = intent.getStringExtra("accountAddress");
        String accountPostcode = intent.getStringExtra("accountPostcode");
        String accountCity = intent.getStringExtra("accountCity");
        String accountProvince = intent.getStringExtra("accountProvince");
        String accountCountry = intent.getStringExtra("accountCountry");
        double latitude = intent.getDoubleExtra("accountLatitude", 0);
        double longitude = intent.getDoubleExtra("accountLongitude", 0);
        double distanceAway = intent.getDoubleExtra("accountDistanceAway", 0);
        int relevance = intent.getIntExtra("accountRelevance", 0);

        Account account = new Account();
        account.setId(accountId);
        account.setName(accountName);
        account.setType(accountType);
        account.setAddress(accountAddress);
        account.setPostalCode(accountPostcode);
        account.setCity(accountCity);
        account.setProvince(accountProvince);
        account.setCountry(accountCountry);
        account.setLatitude(latitude);
        account.setLongitude(longitude);
        account.setDistanceTo(distanceAway);
        account.setRelevanceScore(relevance);

        return account;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        // Add a marker to the map
        LatLng position = new LatLng(account.getLatitude(), account.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(account.getName());
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(
                R.drawable.marker_neutral_24g);
        markerOptions.icon(markerIcon);
        googleMap.addMarker(markerOptions);

        // marker.showInfoWindow();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
    }


    public void displayParticipations(List<Participation> participationList){
        statusMessageLayout.setVisibility(View.GONE);
        this.loadingIndicatorLayout.setVisibility(View.GONE);
        this.participationListRecyclerView.setAlpha(1);
        this.adapter.setData(participationList);
    }


    public void displayMessage(String message){
        this.participationListRecyclerView.setAlpha(0);
        statusMessageTextView.setText(message);
        statusMessageLayout.setVisibility(View.VISIBLE);
        this.loadingIndicatorLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}
