package app.smartpath.android.smartpath.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;
import app.smartpath.android.smartpath.misc.BottomNavigationBarClickListener;
import app.smartpath.android.smartpath.misc.BottomNavigationBarClickListenerFactory;
import app.smartpath.android.smartpath.misc.SmartPathApplication;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView userPortraitImageView;
    private TextView userNameTextView;
    private TextView userPositionTextView;
    private TextView userScoreTextView;

    private LinearLayout rankingWidgetBar;

    private BottomNavigationView bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Store references to views
        userPortraitImageView = (ImageView)findViewById(R.id.iv_user_portrait);
        userNameTextView = (TextView)findViewById(R.id.tv_user_name);
        userPositionTextView = (TextView) findViewById(R.id.tv_user_position);
        userScoreTextView = (TextView) findViewById(R.id.tv_user_score);

        rankingWidgetBar = (LinearLayout)findViewById(R.id.ll_ranking);
        rankingWidgetBar.setOnClickListener(this);

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
        }
    }


    public class UserProfileAsyncHttpRequest extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((SmartPathApplication)ProfileActivity.this.getApplication()).getUserId();

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
