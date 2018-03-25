package io.atactic.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.atactic.android.R;
import io.atactic.android.connect.HttpRequestHandler;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.element.AtacticApplication;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView userPortraitImageView;
    private TextView userNameTextView;
    private TextView userPositionTextView;
    private TextView userScoreTextView;

    private LinearLayout rankingWidgetBar;
    private LinearLayout closeSessionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Store references to views
        userPortraitImageView = findViewById(R.id.iv_user_portrait);
        userNameTextView = findViewById(R.id.tv_user_name);
        userPositionTextView = findViewById(R.id.tv_user_position);
        userScoreTextView = findViewById(R.id.tv_user_score);

        rankingWidgetBar = findViewById(R.id.ll_ranking);
        rankingWidgetBar.setOnClickListener(this);
        closeSessionBar = findViewById(R.id.ll_close_session);
        closeSessionBar.setOnClickListener(this);

        /*
         * Get the reference to the bottom navigation bar. Update click listener and selected item
         */
        BottomNavigationView bottomNavigationBar = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setSelectedItemId(R.id.action_profile);
        bottomNavigationBar.setOnNavigationItemSelectedListener(
                BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));

        new UserProfileAsyncHttpRequest().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_ranking){
            // Toast.makeText(this, "Ranking clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ProfileActivity.this, RankingActivity.class);
            startActivity(i);

        }else if (v.getId() == R.id.ll_close_session){
            Intent i = new Intent(ProfileActivity.this, SimpleLoginActivity.class);
            startActivity(i);
        }
    }


    public class UserProfileAsyncHttpRequest extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication)ProfileActivity.this.getApplication()).getUserId();

            JSONObject response = HttpRequestHandler.sendUserProfileRequest(userId);

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                String fullName = jsonObject.getString("firstName") + " "
                        + jsonObject.getString("lastName");

                String pointsLiteral = getResources().getString(R.string.points);

                userNameTextView.setText(fullName);
                userPositionTextView.setText(jsonObject.getString("position"));
                userScoreTextView.setText(jsonObject.getString("score") + " " + pointsLiteral);

            }catch (JSONException jse){
                jse.printStackTrace();
            }
        }
    }

}
