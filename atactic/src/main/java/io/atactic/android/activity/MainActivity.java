package io.atactic.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import io.atactic.android.R;
import io.atactic.android.fragment.AccountListFragment;
import io.atactic.android.fragment.CampaignListFragment;
import io.atactic.android.fragment.LogoutResponder;
import io.atactic.android.fragment.MapFragment;
import io.atactic.android.fragment.ProfileFragment;
import io.atactic.android.utils.CredentialsCache;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements LogoutResponder {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /*
     * The Main Activity contains an instance of all the Fragments it manages
     */
    private final CampaignListFragment campaignListFragment = new CampaignListFragment();
    private final AccountListFragment accountListFragment = new AccountListFragment();
    private final MapFragment mapFragment = new MapFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    /*
     * This property will contain a reference to the active fragment
     */
    private Fragment activeFragment;

    /**
     * This BottomNavigationView SelectedItemListener will manage navigation from one section segment
     *  to another.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_campaigns:
                    setTitle(R.string.title_activity_quest_list);
                    activeFragment = campaignListFragment;
                    break;
                case R.id.navigation_accounts:
                    setTitle(R.string.title_activity_target_list);
                    activeFragment = accountListFragment;
                    break;
                case R.id.navigation_map:
                    setTitle(R.string.title_activity_map);
                    activeFragment = mapFragment;
                    break;
                case R.id.navigation_profile:
                    setTitle(R.string.title_activity_profile);
                    activeFragment = profileFragment;
                    break;
            }

            // Create and animate a replacement fragment transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container, activeFragment).show(activeFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.title_activity_quest_list);

        // Add all fragments to be managed by this Activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction initialization = fragmentManager.beginTransaction();
        initialization.add(R.id.fragment_container, campaignListFragment);
        initialization.add(R.id.fragment_container, accountListFragment).hide(accountListFragment);
        initialization.add(R.id.fragment_container, mapFragment).hide(mapFragment);
        initialization.add(R.id.fragment_container, profileFragment).hide(profileFragment);
        initialization.commit();

        // Add Navigation Listener to Bottom Navigation Bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void doLogout() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }


}
