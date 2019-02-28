package io.atactic.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.atactic.android.R;
import io.atactic.android.manager.ConfigurationManager;
import io.atactic.android.datahandler.ProfileManager;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.model.TenantConfiguration;
import io.atactic.android.utils.CredentialsCache;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "ProfileActivity";

    private ImageView userPortraitImageView;
    private TextView userNameTextView;
    private TextView userPositionTextView;
    private TextView userScoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Store references to views
        userPortraitImageView = findViewById(R.id.iv_user_portrait);
        userNameTextView = findViewById(R.id.tv_user_name);
        userPositionTextView = findViewById(R.id.tv_user_position);
        userScoreTextView = findViewById(R.id.tv_user_score);

        // Get references to menu options and set this class as click listener

        TextView activityRegisterMenuOption = findViewById(R.id.tv_activity_register);
        activityRegisterMenuOption.setOnClickListener(this);

        TextView changePasswordMenuOption = findViewById(R.id.tv_change_password);
        changePasswordMenuOption.setOnClickListener(this);

        TextView exitMenuOption = findViewById(R.id.tv_exit);
        exitMenuOption.setOnClickListener(this);

        /*
         * Get the reference to the bottom navigation bar. Update click listener and selected item
         */
        BottomNavigationView bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setSelectedItemId(R.id.action_profile);
        bottomNavigationBar.setOnNavigationItemSelectedListener(
                BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));

        // Get Data
        // new ProfileManager(this).getData();

        TenantConfiguration config = ConfigurationManager.getInstance().getConfiguration();
        if (config != null ){
            Log.v(LOG_TAG, "Configuration Data recovered");
            Log.d(LOG_TAG, "CheckInEnabled: " + config.isCheckInEnabled());
        } else {
            Log.w(LOG_TAG, "Configuration is null");
        }
    }


    /**
     * Display data on screen
     *
     * @param profileData User profile data to display
     */
    public void setData(JSONObject profileData){
        try {
            String fullName = profileData.getString("firstName") + " "
                    + profileData.getString("lastName");

            String scoreStr = profileData.getString("score");

            // Set user name label
            userNameTextView.setText(fullName);

            // Set user's position in the company
            userPositionTextView.setText(profileData.getString("position"));

            // Set score, including the customizable literal for "points"
            userScoreTextView.setText(scoreStr);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }


    /**
     * Defined so that this class can be set as a click listener. Listens to clicks to the different
     *  options on the settings menu and defines UI behavior accordingly.
     *
     * @param v Clicked view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_activity_register) {
            // Toast.makeText(this, "Activity history clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ProfileActivity.this, HistoryActivity.class);
            startActivity(i);

        }else if (v.getId() == R.id.tv_change_password) {
            /// Toast.makeText(this, "Change Password clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(i);

        }else if (v.getId() == R.id.tv_exit){
            CredentialsCache.crearAll(ProfileActivity.this);
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

}
