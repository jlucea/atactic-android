package app.smartpath.android.smartpath.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;
import app.smartpath.android.smartpath.misc.BottomNavigationBarClickListener;
import app.smartpath.android.smartpath.misc.SmartPathApplication;

public class ProfileActivity extends AppCompatActivity {

    private ImageView userPortraitImageView;
    private TextView userNameTextView;
    private TextView userPositionTextView;
    private TextView userScoreTextView;
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

        /*
         * Get the reference to the bottom navigation bar and add an ItemSelectedListener
         */
        bottomNavigationBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(
                new BottomNavigationBarClickListener(getBaseContext(),this.getClass()));

        new UserProfileAsyncHttpRequest().execute();
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
